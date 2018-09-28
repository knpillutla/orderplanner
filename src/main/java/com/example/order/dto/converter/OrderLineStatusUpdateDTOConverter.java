package com.example.order.dto.converter;

import org.springframework.stereotype.Component;

import com.example.inventory.dto.events.InventoryAllocatedEvent;
import com.example.inventory.dto.responses.InventoryDTO;
import com.example.order.dto.requests.OrderLineInfoDTO;
import com.example.order.service.OrderServiceImpl;
import com.example.packing.dto.events.PackConfirmationEvent;
import com.example.packing.dto.responses.PackDTO;
import com.example.picking.dto.events.PickConfirmationEvent;
import com.example.picking.dto.responses.PickDTO;

@Component
public class OrderLineStatusUpdateDTOConverter {

	public static OrderLineInfoDTO getOrderLineInfoDTO(InventoryAllocatedEvent inventoryAllocatedEvent) {
		InventoryDTO inventoryDTO = inventoryAllocatedEvent.getInventoryDTO();
		OrderLineInfoDTO req = new OrderLineInfoDTO(inventoryDTO.getOrderLineId(), inventoryDTO.getOrderId(),
				inventoryDTO.getOrderLineNbr(), inventoryDTO.getBusName(), inventoryDTO.getLocnNbr(), inventoryDTO.getOrderNbr(), "", "",
				inventoryDTO.getItemBrcd(), inventoryDTO.getBusUnit(), inventoryDTO.getQty());
		return req;
	}

	public static OrderLineInfoDTO getOrderLineInfoDTO(PickConfirmationEvent pickConfirmationEvent) {
		PickDTO pickDTO = pickConfirmationEvent.getPickDTO();
		OrderLineInfoDTO req = new OrderLineInfoDTO(pickDTO.getOrderLineId(), pickDTO.getOrderId(),
				pickDTO.getOrderLineNbr(), pickDTO.getBusName(), pickDTO.getLocnNbr(), pickDTO.getOrderNbr(), "", "",
				pickDTO.getItemBrcd(), pickDTO.getBusUnit(), pickDTO.getQty());
		return req;
	}

	public static OrderLineInfoDTO getOrderLineInfoDTO(PackConfirmationEvent packConfirmationEvent) {
		PackDTO packDTO = packConfirmationEvent.getPackDTO();
		OrderLineInfoDTO req = new OrderLineInfoDTO(packDTO.getOrderLineId(), packDTO.getOrderId(),
				packDTO.getOrderLineNbr(), packDTO.getBusName(), packDTO.getLocnNbr(), packDTO.getOrderNbr(), "", "",
				packDTO.getItemBrcd(), packDTO.getBusUnit(), packDTO.getQty());
		return req;
	}

}
