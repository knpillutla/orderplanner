package com.example.order.exception;

public class InsufficientInventoryException extends Exception{

	public InsufficientInventoryException(String msg) {
		super(msg);
	}
}
