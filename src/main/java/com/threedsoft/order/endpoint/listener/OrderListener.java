package com.threedsoft.order.endpoint.listener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.threedsoft.customer.order.dto.events.CustomerOrderCreatedEvent;
import com.threedsoft.inventory.dto.events.InventoryAllocatedEvent;
import com.threedsoft.inventory.dto.responses.InventoryResourceDTO;
import com.threedsoft.order.dto.converter.CustomerOrderDTOConverter;
import com.threedsoft.order.dto.converter.OrderLineStatusUpdateDTOConverter;
import com.threedsoft.order.dto.requests.OrderLineInfoDTO;
import com.threedsoft.order.dto.responses.OrderResourceDTO;
import com.threedsoft.order.service.OrderService;
import com.threedsoft.order.streams.OrderStreams;
import com.threedsoft.packing.dto.events.PackConfirmationEvent;
import com.threedsoft.packing.dto.responses.PackResourceDTO;
import com.threedsoft.picking.dto.events.PickConfirmationEvent;
import com.threedsoft.picking.dto.responses.PickResourceDTO;
import com.threedsoft.shipping.dto.events.ShipConfirmationEvent;
import com.threedsoft.shipping.dto.events.ShipRoutingCompletedEvent;
import com.threedsoft.shipping.dto.responses.ShipResourceDTO;
import com.threedsoft.util.dto.events.EventResourceConverter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderListener {
	@Autowired
	OrderService orderService;

	@StreamListener(target = OrderStreams.CUSTOMER_ORDERS_OUTPUT, condition = "headers['eventName']=='CustomerOrderCreatedEvent'")
	public void handleNewOrder(CustomerOrderCreatedEvent customerOrderCreatedEvent) { // OrderCreationRequestDTO
																						// orderCreationRequestDTO) {
		log.info("Received CustomerOrderCreatedEvent Msg: {}" + ": at :" + LocalDateTime.now(),
				customerOrderCreatedEvent);
		long startTime = System.currentTimeMillis();
		try {
			orderService.createOrder(CustomerOrderDTOConverter.getOrderCreationRequestDTO(customerOrderCreatedEvent));
			long endTime = System.currentTimeMillis();
			log.info("Completed CustomerOrderCreatedEvent for : " + customerOrderCreatedEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing CustomerOrderCreatedEvent for : " + customerOrderCreatedEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

	@StreamListener(target = OrderStreams.INVENTORY_OUTPUT, condition = "headers['eventName']=='InventoryAllocatedEvent'")
	public void handleAllocatedInventoryEvent(InventoryAllocatedEvent inventoryAllocatedEvent) {
		log.info("Received InventoryAllocatedEvent for: {}" + ": at :" + LocalDateTime.now(), inventoryAllocatedEvent);
		long startTime = System.currentTimeMillis();
		try {
			orderService.updateOrderLineStatusToReserved(
					OrderLineStatusUpdateDTOConverter.getOrderLineInfoDTO(inventoryAllocatedEvent));
			long endTime = System.currentTimeMillis();
			log.info("Completed InventoryAllocatedEvent for: " + inventoryAllocatedEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing InventoryAllocatedEvent for: " + inventoryAllocatedEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

/*	@StreamListener(target = OrderStreams.PICK_OUTPUT, condition = "headers['eventName']=='LowPickEvent'")
	public void handleLowPickEvent(LowPickEvent lowPickEvent) {
		log.info("Received LowPickEvent for: {}" + ": at :" + LocalDateTime.now(), lowPickEvent);
		long startTime = System.currentTimeMillis();
		try {
			OrderFulfillmentResponseDTO orderFulfillmentResponse = orderService
					.startOrderFulfillment(LowPickEventConverter.getOrderFulfillmentRequestDTO(lowPickEvent));
			log.info("output of LowPickEvent event:" + orderFulfillmentResponse);
			long endTime = System.currentTimeMillis();
			log.info("Completed LowPickEvent for: " + lowPickEvent + ": at :" + LocalDateTime.now() + " : total time:"
					+ (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing LowPickEvent for: " + lowPickEvent + ": at :" + LocalDateTime.now()
					+ " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}*/

	@StreamListener(target = OrderStreams.SHIP_OUTPUT, condition = "headers['eventName']=='ShipRoutingCompletedEvent'")
	public void handleShipRoutingCompletedEventEvent(ShipRoutingCompletedEvent shipRoutingCompletedEvent) {
		log.info("Received ShipRoutingCompletedEvent for: {}" + ": at :" + LocalDateTime.now(),
				shipRoutingCompletedEvent);
		long startTime = System.currentTimeMillis();
		try {
			ShipResourceDTO shipResourceDTO = (ShipResourceDTO) EventResourceConverter
					.getObject(shipRoutingCompletedEvent.getEventResource(), shipRoutingCompletedEvent.getEventResourceClassName());
			OrderResourceDTO orderDTO = orderService.updateRoutingCompleted(
					shipResourceDTO.getBusName(),
					shipResourceDTO.getLocnNbr(),
					shipResourceDTO.getOrderId());
			log.info("output of ShipRoutingCompletedEvent event:" + orderDTO);
			long endTime = System.currentTimeMillis();
			log.info("Completed ShipRoutingCompletedEvent for: " + shipRoutingCompletedEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing ShipRoutingCompletedEvent for: " + shipRoutingCompletedEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}
	
	@StreamListener(target = OrderStreams.PICK_OUTPUT, condition = "headers['eventName']=='PickConfirmationEvent'")
	public void handlePickConfirmationEvent(PickConfirmationEvent pickConfirmationEvent) {
		log.info("Received PickConfirmationEvent for: {}" + ": at :" + LocalDateTime.now(),
				pickConfirmationEvent);
		long startTime = System.currentTimeMillis();
		try {
			PickResourceDTO pickResourceDTO = (PickResourceDTO) EventResourceConverter
					.getObject(pickConfirmationEvent.getEventResource(), pickConfirmationEvent.getEventResourceClassName());
			OrderLineInfoDTO req = new OrderLineInfoDTO();
			req.setBusName(pickResourceDTO.getBusName());
			req.setLocnNbr(pickResourceDTO.getLocnNbr());
			req.setCompany(pickResourceDTO.getCompany());
			req.setDivision(pickResourceDTO.getDivision());
			req.setBusUnit(pickResourceDTO.getBusUnit());
			req.setOrderId(pickResourceDTO.getOrderId());
			req.setOrderNbr(pickResourceDTO.getOrderNbr());
			req.setOrderLineNbr(pickResourceDTO.getOrderLineNbr());
			req.setId(pickResourceDTO.getOrderLineId());
			OrderResourceDTO orderDTO = orderService.updateOrderLineStatusToPicked(req);
			log.info("output of PickConfirmationEvent event:" + orderDTO);
			long endTime = System.currentTimeMillis();
			log.info("Completed PickConfirmationEvent for: " + pickConfirmationEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing PickConfirmationEvent for: " + pickConfirmationEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}
	
	@StreamListener(target = OrderStreams.PACK_OUTPUT, condition = "headers['eventName']=='PackConfirmationEvent'")
	public void handlePackConfirmationEventEvent(PackConfirmationEvent packConfirmationEvent) {
		log.info("Received PackConfirmationEvent for: {}" + ": at :" + LocalDateTime.now(),
				packConfirmationEvent);
		long startTime = System.currentTimeMillis();
		try {
			OrderLineInfoDTO req = new OrderLineInfoDTO();
			PackResourceDTO packResourceDTO = (PackResourceDTO) EventResourceConverter
					.getObject(packConfirmationEvent.getEventResource(), packConfirmationEvent.getEventResourceClassName());
			req.setBusName(packResourceDTO.getBusName());
			req.setLocnNbr(packResourceDTO.getLocnNbr());
			req.setCompany(packResourceDTO.getCompany());
			req.setDivision(packResourceDTO.getDivision());
			req.setBusUnit(packResourceDTO.getBusUnit());
			req.setOrderId(packResourceDTO.getOrderId());
			req.setOrderNbr(packResourceDTO.getOrderNbr());
			req.setOrderLineNbr(packResourceDTO.getOrderLineNbr());
			req.setId(packResourceDTO.getOrderLineId());
			OrderResourceDTO orderDTO = orderService.updateOrderLineStatusToPacked(req);
			log.info("output of PackConfirmationEvent event:" + orderDTO);
			long endTime = System.currentTimeMillis();
			log.info("Completed PackConfirmationEvent for: " + packConfirmationEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing PackConfirmationEvent for: " + packConfirmationEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

	@StreamListener(target = OrderStreams.SHIP_OUTPUT, condition = "headers['eventName']=='ShipConfirmationEvent'")
	public void handleShipConfirmationEvent(ShipConfirmationEvent shipConfirmationEvent) {
		log.info("Received ShipConfirmationEvent for: {}" + ": at :" + LocalDateTime.now(),
				shipConfirmationEvent);
		long startTime = System.currentTimeMillis();
		try {
			ShipResourceDTO shipDTO = (ShipResourceDTO) EventResourceConverter
					.getObject(shipConfirmationEvent.getEventResource(), shipConfirmationEvent.getEventResourceClassName());
			OrderResourceDTO orderDTO = orderService.updateOrderStatusToShipped(shipDTO.getBusName(), shipDTO.getLocnNbr(), shipDTO.getOrderId(), shipDTO.getShipCarrier(), shipDTO.getShipCarrierService(), shipDTO.getTrackingNbr());
			log.info("output of ShipConfirmationEvent event:" + orderDTO);
			long endTime = System.currentTimeMillis();
			log.info("Completed ShipConfirmationEvent for: " + shipConfirmationEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing ShipConfirmationEvent for: " + shipConfirmationEvent + ": at :"
					+ LocalDateTime.now() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}	
	
}
