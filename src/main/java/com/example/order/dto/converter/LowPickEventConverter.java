package com.example.order.dto.converter;

import com.example.order.dto.requests.OrderFulfillmentRequestDTO;
import com.example.picking.dto.events.LowPickEvent;

public class LowPickEventConverter {
	public static OrderFulfillmentRequestDTO getOrderFulfillmentRequestDTO(LowPickEvent lowPickEvent) {
		OrderFulfillmentRequestDTO req = new OrderFulfillmentRequestDTO();
		req.setBusName(lowPickEvent.getBusName());
		req.setLocnNbr(lowPickEvent.getLocnNbr());
		req.setCompany(lowPickEvent.getCompany());
		req.setDivision(lowPickEvent.getDivision());
		req.setBusUnit(lowPickEvent.getBusUnit());
		req.setUserId(lowPickEvent.getEventName());
		return req;
	}

}
