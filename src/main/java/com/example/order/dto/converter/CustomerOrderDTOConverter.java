package com.example.order.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.order.dto.events.CustomerOrderCreatedEvent;
import com.example.order.dto.requests.OrderCreationRequestDTO;
import com.example.order.dto.requests.OrderLineCreationRequestDTO;
import com.example.order.dto.responses.CustomerOrderDTO;
import com.example.order.dto.responses.CustomerOrderLineDTO;

@Component
public class CustomerOrderDTOConverter {

	public static OrderCreationRequestDTO getOrderCreationRequestDTO(CustomerOrderCreatedEvent customerOrderCreatedEvent) {
		CustomerOrderDTO customerOrderDTO = customerOrderCreatedEvent.getCustomerOrderDTO();
		List<OrderLineCreationRequestDTO> orderlineDTOReqList = new ArrayList();
		for (CustomerOrderLineDTO customerOrderLineDTO : customerOrderDTO.getOrderLines()) {
			OrderLineCreationRequestDTO orderlineReqDTO = new OrderLineCreationRequestDTO(
					customerOrderLineDTO.getOrderLineNbr(), customerOrderLineDTO.getItemBrcd(),
					customerOrderLineDTO.getOrigOrderQty(), customerOrderLineDTO.getOrderQty(),
					customerOrderLineDTO.getRefField1(), customerOrderLineDTO.getRefField2());
			orderlineDTOReqList.add(orderlineReqDTO);
		}
		OrderCreationRequestDTO reqDTO = new OrderCreationRequestDTO(customerOrderDTO.getBusName(),
				customerOrderDTO.getLocnNbr(), customerOrderDTO.getCompany(), customerOrderDTO.getDivision(),
				customerOrderDTO.getBusUnit(), customerOrderDTO.getExternalBatchNbr(), customerOrderDTO.getOrderNbr(),
				customerOrderDTO.getOrderDttm(), customerOrderDTO.getShipByDttm(),
				customerOrderDTO.getExpectedDeliveryDttm(), customerOrderDTO.getDeliveryType(),
				customerOrderDTO.isGift(), customerOrderDTO.getGiftMsg(), customerOrderDTO.getSource(),
				customerOrderDTO.getTransactionName(), customerOrderDTO.getRefField1(), customerOrderDTO.getRefField2(),
				customerOrderDTO.getUpdatedBy(), orderlineDTOReqList);
		return reqDTO;
	}
}
