package com.threedsoft.order.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.threedsoft.order.db.Order;
import com.threedsoft.order.db.OrderLine;
import com.threedsoft.order.dto.requests.OrderCreationRequestDTO;
import com.threedsoft.order.dto.requests.OrderLineCreationRequestDTO;
import com.threedsoft.order.dto.requests.OrderUpdateRequestDTO;
import com.threedsoft.order.dto.responses.OrderLineResourceDTO;
import com.threedsoft.order.dto.responses.OrderResourceDTO;
import com.threedsoft.order.util.OrderConstants.OrderLineStatus;
import com.threedsoft.order.util.OrderConstants.OrderStatus;

@Component
public class OrderDTOConverter {

	public OrderResourceDTO getOrderDTO(Order orderEntity) {
		List<OrderLineResourceDTO> orderLineDTOList = new ArrayList();
		for (OrderLine orderLine : orderEntity.getOrderLines()) {
			OrderLineResourceDTO orderLineDTO = this.getOrderLineDTO(orderLine);
			orderLineDTOList.add(orderLineDTO);
		}
		OrderResourceDTO orderDTO = new OrderResourceDTO(orderEntity.getId(), orderEntity.getBusName(), orderEntity.getLocnNbr(),
				orderEntity.getCompany(), orderEntity.getDivision(), orderEntity.getBusUnit(),
				orderEntity.getExternalBatchNbr(), orderEntity.getBatchNbr(), orderEntity.getOrderNbr(),
				orderEntity.getStatCode(), orderEntity.getOrderDttm(), orderEntity.getShipByDttm(),
				orderEntity.getExpectedDeliveryDttm(), orderEntity.getDeliveryType(), orderEntity.getIsGift(),
				orderEntity.getGiftMsg(), orderEntity.getSource(), orderEntity.getTransactionName(),
				orderEntity.getRefField1(), orderEntity.getRefField2(), orderEntity.getUpdatedDttm(),
				orderEntity.getUpdatedBy(), orderEntity.getDelFirstName(), orderEntity.getDelLastName(),
				orderEntity.getDelMiddleName(), orderEntity.getDelAddr1(), orderEntity.getDelAddr2(),
				orderEntity.getDelAddr3(), orderEntity.getDelCity(), orderEntity.getDelState(),
				orderEntity.getDelCountry(), orderEntity.getDelZipcode(), orderEntity.getDelPhoneNbr(),
				orderEntity.getShipCarrier(), orderEntity.getShipService(), orderEntity.getTrackingNbr(), orderLineDTOList);
		return orderDTO;
	}

	public Order getOrderEntity(OrderCreationRequestDTO orderCreationRequestDTO) {
		Order orderEntity = new Order(orderCreationRequestDTO.getBusName(), orderCreationRequestDTO.getLocnNbr(),
				orderCreationRequestDTO.getCompany(), orderCreationRequestDTO.getDivision(),
				orderCreationRequestDTO.getBusUnit(), orderCreationRequestDTO.getExternalBatchNbr(),
				orderCreationRequestDTO.getOrderNbr(), orderCreationRequestDTO.getOrderDttm(),
				orderCreationRequestDTO.getShipByDttm(), orderCreationRequestDTO.getExpectedDeliveryDttm(),
				orderCreationRequestDTO.getDeliveryType(), orderCreationRequestDTO.isGift(),
				orderCreationRequestDTO.getGiftMsg(), orderCreationRequestDTO.getSource(),
				orderCreationRequestDTO.getTransactionName(), orderCreationRequestDTO.getRefField1(),
				orderCreationRequestDTO.getRefField2(), orderCreationRequestDTO.getUserId());
		List<OrderLine> orderLineList = new ArrayList();
		for (OrderLineCreationRequestDTO orderLineCreationRequestDTO : orderCreationRequestDTO.getOrderLines()) {
			OrderLine orderLineEntity = getOrderLineEntity(orderLineCreationRequestDTO, orderCreationRequestDTO);
			orderLineEntity.setStatCode(OrderLineStatus.READY.getStatCode());
			orderEntity.addOrderLine(orderLineEntity);
			orderLineEntity.setOrder(orderEntity);
		}
		orderEntity.setStatCode(OrderStatus.READY.getStatCode());
		return orderEntity;
	}

	public Order updateOrderEntity(Order orderEntity, OrderUpdateRequestDTO orderUpdateReqDTO) {
		orderEntity.setExpectedDeliveryDttm(orderUpdateReqDTO.getExpectedDeliveryDttm());
		orderEntity.setDeliveryType(orderUpdateReqDTO.getDeliveryType());
		orderEntity.setIsGift(orderUpdateReqDTO.getIsGift());
		orderEntity.setGiftMsg(orderUpdateReqDTO.getGiftMsg());
		orderEntity.setShipByDttm(orderUpdateReqDTO.getShipByDttm());
		orderEntity.setTransactionName(orderUpdateReqDTO.getTransactionName());
		orderEntity.setUpdatedBy(orderUpdateReqDTO.getUserId());
		orderEntity.setRefField1(orderUpdateReqDTO.getRefField1());
		orderEntity.setRefField2(orderUpdateReqDTO.getRefField2());
		orderEntity.setSource(orderUpdateReqDTO.getSource());
		return orderEntity;
	}

	public OrderLineResourceDTO getOrderLineDTO(OrderLine orderLine) {
		OrderLineResourceDTO orderLineDTO = new OrderLineResourceDTO(orderLine.getId(), orderLine.getLocnNbr(),
				orderLine.getOrder().getId(), orderLine.getOrderLineNbr(), orderLine.getItemBrcd(),
				orderLine.getOrigOrderQty(), orderLine.getOrderQty(), orderLine.getCancelledQty(),
				orderLine.getShortQty(), orderLine.getPickedQty(), orderLine.getPackedQty(), orderLine.getShippedQty(),
				orderLine.getStatCode(), orderLine.getPackageNbr(), orderLine.getSource(),
				orderLine.getTransactionName(), orderLine.getRefField1(), orderLine.getRefField2(),
				orderLine.getUpdatedDttm(), orderLine.getUpdatedBy(), orderLine.getItemWidth(),
				orderLine.getItemHeight(), orderLine.getItemLength(), orderLine.getItemUnitWt(),
				orderLine.getItemUnitVol());
		return orderLineDTO;
	}

	public OrderLine getOrderLineEntity(OrderLineCreationRequestDTO orderLineCreationRequestDTO,
			OrderCreationRequestDTO orderCreationRequestDTO) {
		OrderLine orderLine = new OrderLine(orderCreationRequestDTO.getLocnNbr(),
				orderLineCreationRequestDTO.getOrderLineNbr(), orderLineCreationRequestDTO.getItemBrcd(),
				orderLineCreationRequestDTO.getOrigOrderQty(), orderLineCreationRequestDTO.getOrderQty(),
				orderCreationRequestDTO.getSource(), orderCreationRequestDTO.getTransactionName(),
				orderLineCreationRequestDTO.getRefField1(), orderLineCreationRequestDTO.getRefField2(),
				orderCreationRequestDTO.getUserId());
		return orderLine;
	}

}
