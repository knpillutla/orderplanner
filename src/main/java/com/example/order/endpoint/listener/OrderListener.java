package com.example.order.endpoint.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import com.example.inventory.dto.events.InventoryAllocatedEvent;
import com.example.order.dto.converter.CustomerOrderDTOConverter;
import com.example.order.dto.converter.OrderLineStatusUpdateDTOConverter;
import com.example.order.dto.events.CustomerOrderCreatedEvent;
import com.example.order.dto.requests.OrderLineInfoDTO;
import com.example.order.dto.responses.OrderDTO;
import com.example.order.service.OrderService;
import com.example.order.service.OrderServiceImpl.OrderLineStatus;
import com.example.order.streams.OrderStreams;
import com.example.packing.dto.events.PackConfirmationEvent;
import com.example.picking.dto.events.PickConfirmationEvent;
import com.example.shipping.dto.events.ShipConfirmationEvent;
import com.example.shipping.dto.events.ShipRoutingCompletedEvent;
import com.example.shipping.dto.responses.ShipDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderListener {
	@Autowired
	OrderService orderService;

	@StreamListener(target = OrderStreams.CUSTOMER_ORDERS_OUTPUT, condition = "headers['eventName']=='CustomerOrderCreatedEvent'")
	public void handleNewOrder(CustomerOrderCreatedEvent customerOrderCreatedEvent) { // OrderCreationRequestDTO
																						// orderCreationRequestDTO) {
		log.info("Received CustomerOrderCreatedEvent Msg: {}" + ": at :" + new java.util.Date(),
				customerOrderCreatedEvent);
		long startTime = System.currentTimeMillis();
		try {
			orderService.createOrder(CustomerOrderDTOConverter.getOrderCreationRequestDTO(customerOrderCreatedEvent));
			long endTime = System.currentTimeMillis();
			log.info("Completed CustomerOrderCreatedEvent for : " + customerOrderCreatedEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing CustomerOrderCreatedEvent for : " + customerOrderCreatedEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

	@StreamListener(target = OrderStreams.INVENTORY_OUTPUT, condition = "headers['eventName']=='InventoryAllocatedEvent'")
	public void handleAllocatedInventoryEvent(InventoryAllocatedEvent inventoryAllocatedEvent) {
		log.info("Received InventoryAllocatedEvent for: {}" + ": at :" + new java.util.Date(), inventoryAllocatedEvent);
		long startTime = System.currentTimeMillis();
		try {
			orderService.updateOrderLineStatusToReserved(
					OrderLineStatusUpdateDTOConverter.getOrderLineInfoDTO(inventoryAllocatedEvent));
			long endTime = System.currentTimeMillis();
			log.info("Completed InventoryAllocatedEvent for: " + inventoryAllocatedEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing InventoryAllocatedEvent for: " + inventoryAllocatedEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

/*	@StreamListener(target = OrderStreams.PICK_OUTPUT, condition = "headers['eventName']=='LowPickEvent'")
	public void handleLowPickEvent(LowPickEvent lowPickEvent) {
		log.info("Received LowPickEvent for: {}" + ": at :" + new java.util.Date(), lowPickEvent);
		long startTime = System.currentTimeMillis();
		try {
			OrderFulfillmentResponseDTO orderFulfillmentResponse = orderService
					.startOrderFulfillment(LowPickEventConverter.getOrderFulfillmentRequestDTO(lowPickEvent));
			log.info("output of LowPickEvent event:" + orderFulfillmentResponse);
			long endTime = System.currentTimeMillis();
			log.info("Completed LowPickEvent for: " + lowPickEvent + ": at :" + new java.util.Date() + " : total time:"
					+ (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing LowPickEvent for: " + lowPickEvent + ": at :" + new java.util.Date()
					+ " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}*/

	@StreamListener(target = OrderStreams.SHIP_OUTPUT, condition = "headers['eventName']=='ShipRoutingCompletedEvent'")
	public void handleShipRoutingCompletedEventEvent(ShipRoutingCompletedEvent shipRoutingCompletedEvent) {
		log.info("Received ShipRoutingCompletedEvent for: {}" + ": at :" + new java.util.Date(),
				shipRoutingCompletedEvent);
		long startTime = System.currentTimeMillis();
		try {
			OrderDTO orderDTO = orderService.updateRoutingCompleted(
					shipRoutingCompletedEvent.getShipDTO().getBusName(),
					shipRoutingCompletedEvent.getShipDTO().getLocnNbr(),
					shipRoutingCompletedEvent.getShipDTO().getOrderId());
			log.info("output of ShipRoutingCompletedEvent event:" + orderDTO);
			long endTime = System.currentTimeMillis();
			log.info("Completed ShipRoutingCompletedEvent for: " + shipRoutingCompletedEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing ShipRoutingCompletedEvent for: " + shipRoutingCompletedEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}
	
	@StreamListener(target = OrderStreams.PICK_OUTPUT, condition = "headers['eventName']=='PickConfirmationEvent'")
	public void handlePickConfirmationEvent(PickConfirmationEvent pickConfirmationEvent) {
		log.info("Received PickConfirmationEvent for: {}" + ": at :" + new java.util.Date(),
				pickConfirmationEvent);
		long startTime = System.currentTimeMillis();
		try {
			OrderLineInfoDTO req = new OrderLineInfoDTO();
			req.setBusName(pickConfirmationEvent.getPickDTO().getBusName());
			req.setLocnNbr(pickConfirmationEvent.getPickDTO().getLocnNbr());
			req.setCompany(pickConfirmationEvent.getPickDTO().getCompany());
			req.setDivision(pickConfirmationEvent.getPickDTO().getDivision());
			req.setBusUnit(pickConfirmationEvent.getPickDTO().getBusUnit());
			req.setOrderId(pickConfirmationEvent.getPickDTO().getOrderId());
			req.setOrderNbr(pickConfirmationEvent.getPickDTO().getOrderNbr());
			req.setOrderLineNbr(pickConfirmationEvent.getPickDTO().getOrderLineNbr());
			req.setId(pickConfirmationEvent.getPickDTO().getOrderLineId());
			OrderDTO orderDTO = orderService.updateOrderLineStatusToPicked(req);
			log.info("output of PickConfirmationEvent event:" + orderDTO);
			long endTime = System.currentTimeMillis();
			log.info("Completed PickConfirmationEvent for: " + pickConfirmationEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing PickConfirmationEvent for: " + pickConfirmationEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}
	
	@StreamListener(target = OrderStreams.PACK_OUTPUT, condition = "headers['eventName']=='PackConfirmationEvent'")
	public void handlePackConfirmationEventEvent(PackConfirmationEvent packConfirmationEvent) {
		log.info("Received PackConfirmationEvent for: {}" + ": at :" + new java.util.Date(),
				packConfirmationEvent);
		long startTime = System.currentTimeMillis();
		try {
			OrderLineInfoDTO req = new OrderLineInfoDTO();
			req.setBusName(packConfirmationEvent.getPackDTO().getBusName());
			req.setLocnNbr(packConfirmationEvent.getPackDTO().getLocnNbr());
			req.setCompany(packConfirmationEvent.getPackDTO().getCompany());
			req.setDivision(packConfirmationEvent.getPackDTO().getDivision());
			req.setBusUnit(packConfirmationEvent.getPackDTO().getBusUnit());
			req.setOrderId(packConfirmationEvent.getPackDTO().getOrderId());
			req.setOrderNbr(packConfirmationEvent.getPackDTO().getOrderNbr());
			req.setOrderLineNbr(packConfirmationEvent.getPackDTO().getOrderLineNbr());
			req.setId(packConfirmationEvent.getPackDTO().getOrderLineId());
			OrderDTO orderDTO = orderService.updateOrderLineStatusToPacked(req);
			log.info("output of PackConfirmationEvent event:" + orderDTO);
			long endTime = System.currentTimeMillis();
			log.info("Completed PackConfirmationEvent for: " + packConfirmationEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing PackConfirmationEvent for: " + packConfirmationEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}

	@StreamListener(target = OrderStreams.SHIP_OUTPUT, condition = "headers['eventName']=='ShipConfirmationEvent'")
	public void handleShipConfirmationEvent(ShipConfirmationEvent shipConfirmationEvent) {
		log.info("Received ShipConfirmationEvent for: {}" + ": at :" + new java.util.Date(),
				shipConfirmationEvent);
		long startTime = System.currentTimeMillis();
		try {
			ShipDTO shipDTO = shipConfirmationEvent.getShipDTO();
			OrderDTO orderDTO = orderService.updateOrderStatusToShipped(shipDTO.getBusName(), shipDTO.getLocnNbr(), shipDTO.getOrderId(), shipDTO.getShipCarrier(), shipDTO.getShipCarrierService(), shipDTO.getTrackingNbr());
			log.info("output of ShipConfirmationEvent event:" + orderDTO);
			long endTime = System.currentTimeMillis();
			log.info("Completed ShipConfirmationEvent for: " + shipConfirmationEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs");
		} catch (Exception e) {
			e.printStackTrace();
			long endTime = System.currentTimeMillis();
			log.error("Error Completing ShipConfirmationEvent for: " + shipConfirmationEvent + ": at :"
					+ new java.util.Date() + " : total time:" + (endTime - startTime) / 1000.00 + " secs", e);
		}
	}	
	
}
