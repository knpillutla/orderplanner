package com.threedsoft.order.service;

import java.util.List;

import com.threedsoft.order.dto.requests.OrderCreationRequestDTO;
import com.threedsoft.order.dto.requests.OrderFulfillmentRequestDTO;
import com.threedsoft.order.dto.requests.OrderLineInfoDTO;
import com.threedsoft.order.dto.requests.OrderUpdateRequestDTO;
import com.threedsoft.order.dto.responses.OrderFulfillmentResourceDTO;
import com.threedsoft.order.dto.responses.OrderResourceDTO;

public interface OrderService {
	public OrderResourceDTO findById(String busName, Integer locnNbr, Long id) throws Exception;
	public OrderResourceDTO createOrder(OrderCreationRequestDTO orderCreationReq) throws Exception;
	public OrderResourceDTO updateOrder(OrderUpdateRequestDTO orderUpdRequest) throws Exception;
	public OrderResourceDTO updateOrderLineStatusToReserved(OrderLineInfoDTO orderLineStatusUpdReq) throws Exception;
	public OrderResourceDTO updateOrderLineStatusToPicked(OrderLineInfoDTO orderLineStatusUpdReq) throws Exception;
	public OrderFulfillmentResourceDTO startOrderFulfillment(OrderFulfillmentRequestDTO orderFulfillmentReq);
	OrderResourceDTO updateRoutingCompleted(String busName, Integer locnNbr, Long orderId) throws Exception;
	OrderResourceDTO updateOrderStatusToShipped(String busName, Integer locnNbr, Long orderId, String shipCarrier,
			String shipService, String trackingNbr) throws Exception;
	OrderResourceDTO updateOrderLineStatusToPacked(OrderLineInfoDTO orderLineStatusUpdReq) throws Exception;
	List<OrderResourceDTO> findByBusNameAndLocnNbr(String busName, Integer locnNbr);
}