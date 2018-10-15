package com.threedsoft.order.dto.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.threedsoft.customer.order.dto.events.CustomerOrderCreatedEvent;
import com.threedsoft.customer.order.dto.requests.CustomerOrderCreationRequestDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderLineResourceDTO;
import com.threedsoft.customer.order.dto.responses.CustomerOrderResourceDTO;
import com.threedsoft.order.dto.requests.OrderCreationRequestDTO;
import com.threedsoft.order.dto.requests.OrderLineCreationRequestDTO;
import com.threedsoft.util.dto.events.EventResourceConverter;

@Component
public class CustomerOrderDTOConverter {

	public static OrderCreationRequestDTO getOrderCreationRequestDTO(CustomerOrderCreatedEvent customerOrderCreatedEvent) {
		CustomerOrderResourceDTO customerOrderDTO = (CustomerOrderResourceDTO) EventResourceConverter
		.getObject(customerOrderCreatedEvent.getEventResource(), customerOrderCreatedEvent.getEventResourceClassName());
		
		//CustomerOrderResourceDTO customerOrderDTO = (CustomerOrderResourceDTO) customerOrderCreatedEvent.getEventResource();
		List<OrderLineCreationRequestDTO> orderlineDTOReqList = new ArrayList();
		for (CustomerOrderLineResourceDTO customerOrderLineDTO : customerOrderDTO.getOrderLines()) {
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
