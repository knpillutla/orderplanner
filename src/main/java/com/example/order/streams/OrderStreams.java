package com.example.order.streams;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface OrderStreams {
	public String CUSTOMER_ORDERS_OUTPUT = "customer-orders-out";
	//public String ORDERS_INPUT = "orders-in";
    public String ORDERS_OUTPUT = "orders-out";
    public String INVENTORY_OUTPUT="inventory-out";
    public String PICK_OUTPUT="pick-out";
    public String PACK_OUTPUT="pack-out";
    public String SHIP_OUTPUT="ship-out";
    
    @Input(CUSTOMER_ORDERS_OUTPUT)
    public SubscribableChannel inboundCustomerOrders();
    
    @Input(INVENTORY_OUTPUT)
    public SubscribableChannel outboundInventory();

    @Input(PICK_OUTPUT)
    public SubscribableChannel outboundPick();

    @Input(PACK_OUTPUT)
    public SubscribableChannel outboundPack();

    @Input(SHIP_OUTPUT)
    public SubscribableChannel outboundShip();

    @Output(ORDERS_OUTPUT)
    public MessageChannel outboundOrders();
}