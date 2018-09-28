package com.example.order.endpoint.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.order.dto.events.OrderCreationFailedEvent;
import com.example.order.dto.events.OrderFulfillmentFailedEvent;
import com.example.order.dto.events.OrderUpdateFailedEvent;
import com.example.order.dto.requests.OrderCreationRequestDTO;
import com.example.order.dto.requests.OrderFulfillmentRequestDTO;
import com.example.order.dto.requests.OrderUpdateRequestDTO;
import com.example.order.service.OrderService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/orders/v1")
@Api(value="Order Service", description="Operations pertaining to Orders")
@RefreshScope
@Slf4j
public class OrderRestEndPoint {

    @Value("${message: Order Service - Config Server is not working..please check}")
    private String msg;
    
    @Autowired
    OrderService orderService;
	
	@GetMapping("/")
	public ResponseEntity hello() throws Exception {
		return ResponseEntity.ok(msg);
	}
	
	@GetMapping("/{busName}/{locnNbr}/order/{id}")
	public ResponseEntity getById(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr, @PathVariable("id") Long id) throws IOException {
		try {
			return ResponseEntity.ok(orderService.findById(busName, locnNbr, id));
		} catch (Exception e) {
			log.error("Error Occured for busName:" + busName + ", id:" + id + " : " + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorRestResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error Occured for GET request busName:" + busName + ", id:" + id + " : " + e.getMessage()));
		}
	}

	@PostMapping("/{busName}/{locnNbr}/order/{id}")
	public ResponseEntity updateOrder(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr, @RequestBody OrderUpdateRequestDTO orderUpdateReq) throws IOException {
		try {
			return ResponseEntity.ok(orderService.updateOrder(orderUpdateReq));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new OrderUpdateFailedEvent(orderUpdateReq, "Error Occured while processing request:" + e.getMessage()));
		}
	}	

	@PutMapping("/{busName}/{locnNbr}/order")
	public ResponseEntity createOrder(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr, @RequestBody OrderCreationRequestDTO orderCreationReq) throws IOException {
		long startTime = System.currentTimeMillis();
		log.info("Received Order Create request for : " + orderCreationReq.toString() + ": at :" + new java.util.Date());
		ResponseEntity resEntity = null;
		try {
			resEntity = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(orderService.createOrder(orderCreationReq));
		} catch (Exception e) {
			e.printStackTrace();
			resEntity = ResponseEntity.badRequest().body(new OrderCreationFailedEvent(orderCreationReq, "Error Occured while processing Inventory Create request:" + e.getMessage()));
		}
		long endTime = System.currentTimeMillis();
		log.info("Completed Order Create request for : " + orderCreationReq.toString() + ": at :" + new java.util.Date() + " : total time:" + (endTime-startTime)/1000.00 + " secs");
		return resEntity;
	}	
	
	@PostMapping("/{busName}/{locnNbr}")
	public ResponseEntity startOrderFulfillment(@PathVariable("busName") String busName, @PathVariable("locnNbr") Integer locnNbr, @RequestBody OrderFulfillmentRequestDTO req) throws IOException {
		try {
			return ResponseEntity.ok(orderService.startOrderFulfillment(req));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new OrderFulfillmentFailedEvent(req, "Error Occured while processing request:" + e.getMessage()));
		}
	}		
}
