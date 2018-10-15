package com.threedsoft.order.util;

public class OrderConstants {
	public static final String ORDERPLANNER_SERVICE_NAME="order";


	public enum OrderRoutingStatus {
		CREATED(0), ERROR(290), COMPLETED(190);
		OrderRoutingStatus(Integer statCode) {
			this.statCode = statCode;
		}

		private Integer statCode;

		public Integer getStatCode() {
			return statCode;
		}
	}
	public enum OrderStatus {
		CREATED(100), READY(110), RELEASED(120), ALLOCATED(130), PARTIALLY_ALLOCATED(131), PICKED(140), PARTIALLY_PICKED(141), PACKED(150), PARTIALLY_PACKED(151), SHIPPED(160),
		SHORTED(170), CANCELLED(199);
		OrderStatus(Integer statCode) {
			this.statCode = statCode;
		}

		private Integer statCode;

		public Integer getStatCode() {
			return statCode;
		}
	}

	public enum OrderLineStatus {
		CREATED(100), READY(110), ALLOCATED(120), PICKED(130), PACKED(140), SHIPPED(150), SHORTED(160), CANCELLED(199);
		OrderLineStatus(Integer statCode) {
			this.statCode = statCode;
		}

		private Integer statCode;

		public Integer getStatCode() {
			return statCode;
		}
	}

}
