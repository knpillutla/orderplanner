package com.threedsoft.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.threedsoft.order.db.Order;
import com.threedsoft.order.db.OrderLine;
import com.threedsoft.order.db.OrderLineRepository;
import com.threedsoft.order.db.OrderRepository;
import com.threedsoft.order.dto.converter.OrderDTOConverter;
import com.threedsoft.order.dto.events.OrderAllocatedEvent;
import com.threedsoft.order.dto.events.OrderCreatedEvent;
import com.threedsoft.order.dto.events.OrderCreationFailedEvent;
import com.threedsoft.order.dto.events.OrderLineAllocationFailedEvent;
import com.threedsoft.order.dto.events.OrderPackedEvent;
import com.threedsoft.order.dto.events.OrderPickedEvent;
import com.threedsoft.order.dto.events.OrderPlannedEvent;
import com.threedsoft.order.dto.events.OrderShippedEvent;
import com.threedsoft.order.dto.events.OrderUpdateFailedEvent;
import com.threedsoft.order.dto.events.SmallStoreOrderPlannedEvent;
import com.threedsoft.order.dto.requests.OrderCreationRequestDTO;
import com.threedsoft.order.dto.requests.OrderFulfillmentRequestDTO;
import com.threedsoft.order.dto.requests.OrderLineInfoDTO;
import com.threedsoft.order.dto.requests.OrderUpdateRequestDTO;
import com.threedsoft.order.dto.responses.OrderFulfillmentResourceDTO;
import com.threedsoft.order.dto.responses.OrderResourceDTO;
import com.threedsoft.order.util.OrderConstants;
import com.threedsoft.order.util.OrderConstants.OrderLineStatus;
import com.threedsoft.order.util.OrderConstants.OrderRoutingStatus;
import com.threedsoft.order.util.OrderConstants.OrderStatus;
import com.threedsoft.util.service.EventPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	OrderRepository orderDAO;

	@Autowired
	OrderLineRepository orderLineDAO;

	@Autowired
	EventPublisher eventPublisher;

	@Autowired
	OrderDTOConverter orderDTOConverter;
	
   private static final AtomicLong sequence = new AtomicLong(System.currentTimeMillis() / 1000);

	@Override
	@Transactional
	public OrderResourceDTO updateOrder(OrderUpdateRequestDTO orderUpdateRequestDTO) throws Exception {
		OrderResourceDTO orderDTO = null;
		try {
			Optional<Order> orderOptional = orderDAO.findById(orderUpdateRequestDTO.getId());
			if (!orderOptional.isPresent()) {
				throw new Exception("Order Update Failed. Order Not found to update");
			}
			Order orderEntity = orderOptional.get();
			orderDTOConverter.updateOrderEntity(orderEntity, orderUpdateRequestDTO);
			orderDTO = orderDTOConverter.getOrderDTO(orderDAO.save(orderEntity));
		} catch (Exception ex) {
			log.error("Created Order Error:" + ex.getMessage(), ex);
			eventPublisher.publish(
					new OrderUpdateFailedEvent(orderUpdateRequestDTO, OrderConstants.ORDERPLANNER_SERVICE_NAME, "Update Order Error:" + ex.getMessage()));
			throw ex;
		}
		return orderDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	@Override
	@Transactional
	public OrderResourceDTO createOrder(OrderCreationRequestDTO orderCreationRequestDTO) throws Exception {
		OrderResourceDTO orderResponseDTO = null;
		try {
			Order order = orderDTOConverter.getOrderEntity(orderCreationRequestDTO);
			order.setStatCode(OrderStatus.CREATED.getStatCode());
			order.setRoutingStatCode(OrderRoutingStatus.CREATED.getStatCode());
			Order savedOrderObj = orderDAO.save(order);
			orderResponseDTO = orderDTOConverter.getOrderDTO(savedOrderObj);
			eventPublisher.publish(new OrderCreatedEvent(orderResponseDTO, OrderConstants.ORDERPLANNER_SERVICE_NAME));
			//this.startOrderFulfillment(order.getOrderNbr(), savedOrderObj, savedOrderObj.getUpdatedBy());
		} catch (Exception ex) {
			log.error("Created Order Error:" + ex.getMessage(), ex);
			eventPublisher.publish(
					new OrderCreationFailedEvent(orderCreationRequestDTO, OrderConstants.ORDERPLANNER_SERVICE_NAME,"Created Order Error:" + ex.getMessage()));
			throw ex;
		}
		return orderResponseDTO;
	}

	@Override
	public OrderResourceDTO findById(String busName, Integer locnNbr, Long id) throws Exception {
		Order orderEntity = orderDAO.findById(busName, locnNbr, id);
		return orderDTOConverter.getOrderDTO(orderEntity);
	}

	@Override
	@Transactional
	public OrderResourceDTO updateOrderLineStatusToReserved(OrderLineInfoDTO orderLineStatusUpdReq)
			throws Exception {
		OrderResourceDTO orderResponseDTO = null;
		try {
			Order orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderNbr(orderLineStatusUpdReq.getBusName(),
					orderLineStatusUpdReq.getLocnNbr(), orderLineStatusUpdReq.getOrderNbr());
			OrderLine orderLine = this.getOrderLine(orderEntity, orderLineStatusUpdReq.getId());
			if(orderLine.getStatCode() > OrderLineStatus.ALLOCATED.getStatCode()) throw new Exception("Cannot update Order line status to Picked. OrderLine already in status >=Allocated status");
			orderLine.setStatCode(OrderLineStatus.ALLOCATED.getStatCode());
			orderEntity.setStatCode(OrderStatus.PARTIALLY_ALLOCATED.getStatCode());
			orderEntity = orderDAO.save(orderEntity);
			
			boolean isEntireOrderReservedForInventory = areAllOrderLinesSameStatus(orderEntity, OrderLineStatus.ALLOCATED.getStatCode());

			if (isEntireOrderReservedForInventory) {
				orderEntity.setStatCode(OrderStatus.ALLOCATED.getStatCode());
				orderEntity = orderDAO.save(orderEntity);
				eventPublisher.publish(new OrderAllocatedEvent(orderDTOConverter.getOrderDTO(orderEntity),OrderConstants.ORDERPLANNER_SERVICE_NAME));
			}
		} catch (Exception ex) {
			log.error("Order Line Allocation Failed Error:" + ex.getMessage(), ex);
			eventPublisher.publish(new OrderLineAllocationFailedEvent(orderLineStatusUpdReq,OrderConstants.ORDERPLANNER_SERVICE_NAME,
					"Order Line Allocation Failed Error:" + ex.getMessage()));
			throw ex;
		}
		return orderResponseDTO;
	}
	
	public OrderLine getOrderLine(Order orderEntity, Long orderDtlId) {
		for (OrderLine orderLine : orderEntity.getOrderLines()) {
			if (orderLine.getId().equals(orderDtlId)) {
				return orderLine;
			}
		}
		return null;
	}

	public boolean areAllOrderLinesSameStatus(Order orderEntity, Integer statCode) {
		for (OrderLine orderLine : orderEntity.getOrderLines()) {
			if (!(orderLine.getStatCode()>=statCode)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public OrderFulfillmentResourceDTO startOrderFulfillment(OrderFulfillmentRequestDTO orderFulfillmentReq) {
		OrderFulfillmentResourceDTO responseDTO = new OrderFulfillmentResourceDTO(orderFulfillmentReq);
		List<OrderResourceDTO> orderDTOList= new ArrayList();
		List orderFailureDTOList= new ArrayList();
		
		String batchNbr = String.valueOf(sequence.incrementAndGet());
		String userId = orderFulfillmentReq.getUserId();
		if(orderFulfillmentReq.getOrderIdList() !=null && orderFulfillmentReq.getOrderIdList().size()>0) {
			// created pick list based on order ids
			// get all the orderids
			for(Long orderId:orderFulfillmentReq.getOrderIdList()) {
				Order orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderId(orderFulfillmentReq.getBusName(), orderFulfillmentReq.getLocnNbr(), orderId);
				OrderResourceDTO orderDTO;
				try {
					orderDTO = startOrderFulfillment(batchNbr, orderEntity, userId);
					orderDTOList.add(orderDTO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					orderFailureDTOList.add(orderId);
				}
			}
		}
		else
		if(orderFulfillmentReq.getOrderNbrList() !=null && orderFulfillmentReq.getOrderNbrList().size()>0) {
			// created pick list based on order Nbrs
			// get all the orderids
			for(String orderNbr:orderFulfillmentReq.getOrderNbrList()) {
				Order orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderNbr(orderFulfillmentReq.getBusName(), orderFulfillmentReq.getLocnNbr(), orderNbr);
				OrderResourceDTO orderDTO;
				try {
					orderDTO = startOrderFulfillment(batchNbr, orderEntity, userId);
					orderDTOList.add(orderDTO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					orderFailureDTOList.add(orderNbr);
				}
			}
		}
		else {
			// create pick list based on number of orders
			int numOfOrders = orderFulfillmentReq.getNumOfOrders();
			if(numOfOrders==0) {
				numOfOrders = 10;
			}
/*			
			String orderSelectionOption = orderFulfillmentReq.getOrderSelectionOption();
			if(orderSelectionOption.equalsIgnoreCase("byAreaZoneAisle")) {
				
			}
			else
			if(orderSelectionOption.equalsIgnoreCase("deliveryType")) {
				
			}
*/			log.info("No options selected, using num of orders to fetch from DB" + numOfOrders);
			List<Order> orderEntityList = orderDAO.findByBusNameAndLocnNbrAndStatCodeOrderByOrderId(orderFulfillmentReq.getBusName(), orderFulfillmentReq.getLocnNbr(), OrderStatus.CREATED.getStatCode());
			log.info("Retreived " + orderEntityList.size() + "from db");
			OrderResourceDTO orderDTO;
			for(Order orderEntity:orderEntityList) {
				try {
					if(orderFulfillmentReq.isSmallStoreMode()) {
						orderDTO = startOrderFulfillmentForSmallStore(batchNbr, orderEntity, userId);
					}else {
					orderDTO = startOrderFulfillment(batchNbr, orderEntity, userId);
					}
					orderDTOList.add(orderDTO);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		responseDTO.setBatchNbr(batchNbr);
		responseDTO.setSuccessList(orderDTOList);
		responseDTO.setFailureList(orderFailureDTOList);
		return responseDTO;
	}
	
	@Transactional
	public OrderResourceDTO startOrderFulfillment(String batchNbr, Order orderEntity, String userId) throws Exception{
		orderEntity.setStatCode(OrderStatus.RELEASED.getStatCode());
		orderEntity.setBatchNbr(batchNbr);
		orderEntity.setUpdatedBy(userId);
		orderEntity = orderDAO.save(orderEntity);
		OrderResourceDTO  orderDTO = orderDTOConverter.getOrderDTO(orderEntity);
		eventPublisher.publish(new OrderPlannedEvent(orderDTO, OrderConstants.ORDERPLANNER_SERVICE_NAME));
		return orderDTO;
	}
	
	@Transactional
	public OrderResourceDTO startOrderFulfillmentForSmallStore(String batchNbr, Order orderEntity, String userId) throws Exception{
		orderEntity.setStatCode(OrderStatus.PACKED.getStatCode());
		orderEntity.setBatchNbr(batchNbr);
		orderEntity.setUpdatedBy(userId);
		orderEntity = orderDAO.save(orderEntity);
		OrderResourceDTO  orderDTO = orderDTOConverter.getOrderDTO(orderEntity);
		eventPublisher.publish(new SmallStoreOrderPlannedEvent(orderDTO, OrderConstants.ORDERPLANNER_SERVICE_NAME));
		return orderDTO;
	}

	@Override
	@Transactional
	public OrderResourceDTO updateRoutingCompleted(String busName, Integer locnNbr, Long orderId) throws Exception {
		Order orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderId(busName, locnNbr, orderId);
		orderEntity.setRoutingStatCode(OrderRoutingStatus.COMPLETED.getStatCode());
		//orderEntity.setStatCode(OrderStatus.SHIPPED.getStatCode());
		orderDAO.save(orderEntity);
		OrderResourceDTO  orderDTO = orderDTOConverter.getOrderDTO(orderEntity);
		//eventPublisher.publish(new SmallStoreOrderPlannedEvent(orderDTO));
		return orderDTO;
	}

	@Override
	@Transactional
	public OrderResourceDTO updateOrderLineStatusToPicked(OrderLineInfoDTO orderLineInfo)
			throws Exception {
		OrderResourceDTO orderResponseDTO = null;
		try {
			Order orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderId(orderLineInfo.getBusName(),
					orderLineInfo.getLocnNbr(), orderLineInfo.getOrderId());
			OrderLine orderLine = this.getOrderLine(orderEntity, orderLineInfo.getId());
			if(orderLine.getStatCode() > OrderLineStatus.PICKED.getStatCode()) throw new Exception("Cannot update Order line status to Picked. OrderLine already in status >=picked status ");
			orderLine.setStatCode(OrderLineStatus.PICKED.getStatCode());
			orderEntity.setStatCode(OrderStatus.PARTIALLY_PICKED.getStatCode());
			orderEntity = orderDAO.save(orderEntity);
			
			boolean isEntireOrderPicked = areAllOrderLinesSameStatus(orderEntity, OrderLineStatus.PICKED.getStatCode());

			if (isEntireOrderPicked) {
				orderEntity.setStatCode(OrderStatus.PICKED.getStatCode());
				orderEntity = orderDAO.save(orderEntity);
				eventPublisher.publish(new OrderPickedEvent(orderDTOConverter.getOrderDTO(orderEntity), OrderConstants.ORDERPLANNER_SERVICE_NAME));
			}
		} catch (Exception ex) {
			log.error("updateOrderLineStatusToPicked Failed Error:" + ex.getMessage(), ex);
			eventPublisher.publish(new OrderLineAllocationFailedEvent(orderLineInfo,OrderConstants.ORDERPLANNER_SERVICE_NAME,
					"updateOrderLineStatusToPicked Failed Error:" + ex.getMessage()));
			throw ex;
		}
		return orderResponseDTO;

	}

	@Override
	@Transactional
	public OrderResourceDTO updateOrderLineStatusToPacked(OrderLineInfoDTO orderLineInfo)
			throws Exception {
		OrderResourceDTO orderResponseDTO = null;
		try {
			Order orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderId(orderLineInfo.getBusName(),
					orderLineInfo.getLocnNbr(), orderLineInfo.getOrderId());
			OrderLine orderLine = this.getOrderLine(orderEntity, orderLineInfo.getId());
			if(orderLine.getStatCode() > OrderLineStatus.PACKED.getStatCode()) throw new Exception("Cannot update Order line status to Packed. OrderLine >=packed status already ");
			orderLine.setStatCode(OrderLineStatus.PACKED.getStatCode());
			orderEntity.setStatCode(OrderStatus.PARTIALLY_PACKED.getStatCode());
			orderEntity = orderDAO.save(orderEntity);
			
			boolean isEntireOrderPicked = areAllOrderLinesSameStatus(orderEntity, OrderLineStatus.PACKED.getStatCode());

			if (isEntireOrderPicked) {
				orderEntity.setStatCode(OrderStatus.PACKED.getStatCode());
				orderEntity = orderDAO.save(orderEntity);
				eventPublisher.publish(new OrderPackedEvent(orderDTOConverter.getOrderDTO(orderEntity),OrderConstants.ORDERPLANNER_SERVICE_NAME));
			}
		} catch (Exception ex) {
			log.error("updateOrderLineStatusToPacked Failed Error:" + ex.getMessage(), ex);
			eventPublisher.publish(new OrderLineAllocationFailedEvent(orderLineInfo,OrderConstants.ORDERPLANNER_SERVICE_NAME,
					"updateOrderLineStatusToPacked Failed Error:" + ex.getMessage()));
			throw ex;
		}
		return orderResponseDTO;

	}

	@Override
	@Transactional
	public OrderResourceDTO updateOrderStatusToShipped(String busName, Integer locnNbr, Long orderId, String shipCarrier, String shipService, String trackingNbr) throws Exception{
		Order orderEntity = orderDAO.findByBusNameAndLocnNbrAndOrderId(busName,locnNbr, orderId);
		if(orderEntity.getStatCode() > OrderLineStatus.SHIPPED.getStatCode()) throw new Exception("Cannot update Order status to Shipped. OrderStatus is >=shipped status already ");
		orderEntity.setStatCode(OrderStatus.SHIPPED.getStatCode());
		orderEntity.setShipCarrier(shipCarrier);
		orderEntity.setShipService(shipService);
		orderEntity.setTrackingNbr(trackingNbr);
		orderEntity = orderDAO.save(orderEntity);
		OrderResourceDTO orderDTO = orderDTOConverter.getOrderDTO(orderEntity);
		eventPublisher.publish(new OrderShippedEvent(orderDTO, OrderConstants.ORDERPLANNER_SERVICE_NAME));
		return orderDTO;
	}

	@Override
	public List<OrderResourceDTO> findByBusNameAndLocnNbr(String busName, Integer locnNbr) {
		PageRequest pageRequest = new PageRequest(0, 20);
		List<Order> orderEntityList = orderDAO.findByBusNameAndLocnNbr(busName, locnNbr, pageRequest);
		List<OrderResourceDTO> orderDTOList = new ArrayList();
		for(Order orderEntity : orderEntityList) {
			orderDTOList.add(orderDTOConverter.getOrderDTO(orderEntity));
		}
		return orderDTOList;
	}
}
