package com.example.order.endpoint.listener;


import org.springframework.beans.factory.annotation.Autowired;

import com.threedsoft.order.streams.OrderStreams;
import com.threedsoft.util.service.EventPublisher;

import lombok.extern.slf4j.Slf4j;

/*@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration*/
@Slf4j
public class OrderListenerTest {
	@Autowired
	private EventPublisher sender;
	
	@Autowired
	private OrderStreams orderStreams;
	
	//@Autowired
	//private MessageCollector messageCollector;

/*	@Test
	@SuppressWarnings("unchecked")
	public void testAccepted() {
		sender.send(createNewOrder());
		Message<OrderCreatedEvent> receivedEvent = (Message) messageCollector.forChannel(orderStreams.outboundOrders()).poll();
		assertNotNull(receivedEvent.getPayload());
		log.info("OrderService response received: {}", receivedEvent.getPayload());
	}*/

/*	@Test
	@SuppressWarnings("unchecked")
	public void testRejected() {
		processor.input().send(MessageBuilder.withPayload(createNewOrder()).build());
		Message received = (Message) messageCollector.forChannel(processor.output()).poll();
		log.info("Order response received: {}", received.getPayload());
		assertNotNull(received.getPayload());
		assertEquals(OrderStatus.REJECTED, received.getPayload().getStatus());
	}*/

/*	public OrderCreationRequestDTO createNewOrder() {
		LocalDateTime currentDate = LocalDateTime.now();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DATE, 5);
		LocalDateTime shipDttm = cal.getTime();
		cal.add(Calendar.DATE, 10);
		LocalDateTime expectedDeliveryDttm = cal.getTime();
		Random rand = new Random();
		int orderNbr = rand.nextInt(10000);
		OrderCreationRequestDTO orderCreationReq = new OrderCreationRequestDTO("AMZ", 1000, "", "", "", "TEST"+orderNbr,
				"FIR0" +orderNbr , currentDate, shipDttm, expectedDeliveryDttm, "Express", false, "", "TestService",
				"TestCreateOrder", "", "", "Krishna");
		return orderCreationReq;
	}*/
}