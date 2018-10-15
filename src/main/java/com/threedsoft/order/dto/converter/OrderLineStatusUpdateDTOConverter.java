package com.threedsoft.order.dto.converter;

import org.springframework.stereotype.Component;

import com.threedsoft.inventory.dto.events.InventoryAllocatedEvent;
import com.threedsoft.inventory.dto.responses.InventoryResourceDTO;
import com.threedsoft.order.dto.requests.OrderLineInfoDTO;
import com.threedsoft.order.service.OrderServiceImpl;
import com.threedsoft.packing.dto.events.PackConfirmationEvent;
import com.threedsoft.packing.dto.responses.PackResourceDTO;
import com.threedsoft.picking.dto.events.PickConfirmationEvent;
import com.threedsoft.picking.dto.responses.PickResourceDTO;
import com.threedsoft.util.dto.events.EventResourceConverter;

@Component
public class OrderLineStatusUpdateDTOConverter {

	public static OrderLineInfoDTO getOrderLineInfoDTO(InventoryAllocatedEvent inventoryAllocatedEvent) {
		InventoryResourceDTO inventoryDTO = (InventoryResourceDTO) EventResourceConverter
				.getObject(inventoryAllocatedEvent.getEventResource(), inventoryAllocatedEvent.getEventResourceClassName());
		OrderLineInfoDTO req = new OrderLineInfoDTO(inventoryDTO.getOrderLineId(), inventoryDTO.getOrderId(),
				inventoryDTO.getOrderLineNbr(), inventoryDTO.getBusName(), inventoryDTO.getLocnNbr(), inventoryDTO.getOrderNbr(), "", "",
				inventoryDTO.getItemBrcd(), inventoryDTO.getBusUnit(), inventoryDTO.getQty());
		return req;
	}

	public static OrderLineInfoDTO getOrderLineInfoDTO(PickConfirmationEvent pickConfirmationEvent) {
		PickResourceDTO pickDTO = (PickResourceDTO) pickConfirmationEvent.getEventResource();
		OrderLineInfoDTO req = new OrderLineInfoDTO(pickDTO.getOrderLineId(), pickDTO.getOrderId(),
				pickDTO.getOrderLineNbr(), pickDTO.getBusName(), pickDTO.getLocnNbr(), pickDTO.getOrderNbr(), "", "",
				pickDTO.getItemBrcd(), pickDTO.getBusUnit(), pickDTO.getQty());
		return req;
	}

	public static OrderLineInfoDTO getOrderLineInfoDTO(PackConfirmationEvent packConfirmationEvent) {
		PackResourceDTO packDTO = (PackResourceDTO) packConfirmationEvent.getEventResource();
		OrderLineInfoDTO req = new OrderLineInfoDTO(packDTO.getOrderLineId(), packDTO.getOrderId(),
				packDTO.getOrderLineNbr(), packDTO.getBusName(), packDTO.getLocnNbr(), packDTO.getOrderNbr(), "", "",
				packDTO.getItemBrcd(), packDTO.getBusUnit(), packDTO.getQty());
		return req;
	}

}
