package com.example.order.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Entity
@Data
@Table(name="ORDERS")
public class Order  implements Serializable{
	@Column(name="ID")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	List<OrderLine> orderLines = new ArrayList<>();
	
	@Column(name="BUS_NAME")
	String busName;

	@Column(name="LOCN_NBR")
	Integer locnNbr;

	@Column(name="COMPANY")
	String company;

	@Column(name="DIVISION")
	String division;

	@Column(name="BUS_UNIT")
	String busUnit;

	@Column(name="EXT_BATCH_NBR")
	String externalBatchNbr;

	@Column(name="BATCH_NBR")
	String batchNbr;

	@Column(name="PACKAGE_NBR")
	String packageNbr;

	@Column(name="ORDER_NBR")
	String orderNbr;

	@Column(name="ORDER_TYPE")
	String orderType;

	@Column(name="STAT_CODE")
	Integer statCode;

	@Column(name="ROUTING_STAT_CODE")
	Integer routingStatCode;

	@Column(name="ORDER_DTTM")
	Date orderDttm;

	@Column(name="SHIP_BY_DTTM")
	Date shipByDttm;

	@Column(name="EXPECTED_DELIVERY_DTTM")
	Date expectedDeliveryDttm;

	@Column(name="DELIVERY_TYPE")
	String deliveryType;

	@Column(name="IS_GIFT")
	boolean isGift;

	@Column(name="GIFT_MSG")
	String giftMsg;

	@Column(name="DEL_FIRST_NAME")
    String delFirstName;

	@Column(name="DEL_LAST_NAME")
    String delLastName;
	
	@Column(name="DEL_MIDDLE_NAME")
    String delMiddleName;
	
	@Column(name="DEL_ADDR_1")
    String delAddr1;
	
	@Column(name="DEL_ADDR_2")
    String delAddr2;
	
	@Column(name="DEL_ADDR_3")
    String delAddr3;
	
	@Column(name="DEL_CITY")
    String delCity;
	
	@Column(name="DEL_STATE")
    String delState;
	
	@Column(name="DEL_COUNTRY")
    String delCountry;
	
	@Column(name="DEL_ZIPCODE")
    String delZipcode;
	
	@Column(name="DEL_PHONE_NBR")
    String delPhoneNbr;
	
	@Column(name="SHIP_CARRIER")
    String shipCarrier;
	
	@Column(name="SHIP_SERVICE")
    String shipService;
	
	@Column(name="TRACKING_NBR")
    String trackingNbr;

	@Column(name="SOURCE")
	String source;

	@Column(name="TRANSACTION_NAME")
	String transactionName;

	@Column(name="REF_FIELD_1")
	String refField1;

	@Column(name="REF_FIELD_2")
	String refField2;

	@Column(name="HOST_NAME")
	String hostName;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
	@Column(name="CREATED_DTTM", nullable = false, updatable = false)
	Date createdDttm;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DTTM", nullable = false)
    @LastModifiedDate
	Date updatedDttm;
	
	@Column(name="CREATED_BY")
	String createdBy;

 	@Column(name="UPDATED_BY")
	String updatedBy;

    public void addOrderLine(OrderLine orderLine) {
    	orderLines.add(orderLine);
    	//orderLine.setOrder(this);
    }
 
    public void removeOrderLine(OrderLine orderLine) {
    	orderLines.remove(orderLine);
    	//orderLine.setOrder(null);
    }

	public Order(String busName, Integer locnNbr, String company, String division, String busUnit,
			String externalBatchNbr, String orderNbr, Date orderDttm, Date shipByDttm, Date expectedDeliveryDttm,
			String deliveryType, boolean isGift, String giftMsg, String source, String transactionName,
			String refField1, String refField2, String userId) {
		this.busName = busName;
		this.locnNbr = locnNbr;
		this.company = company;
		this.division = division;
		this.busUnit = busUnit;
		this.externalBatchNbr = externalBatchNbr;
		this.orderNbr = orderNbr;
		this.orderDttm = orderDttm;
		this.shipByDttm = shipByDttm;
		this.expectedDeliveryDttm = expectedDeliveryDttm;
		this.deliveryType = deliveryType;
		this.isGift = isGift;
		this.giftMsg = giftMsg;
		this.source = source;
		this.transactionName = transactionName;
		this.refField1 = refField1;
		this.refField2 = refField2;
		this.createdBy = userId;
		this.updatedBy = userId;
		this.createdDttm = new java.util.Date();
		this.updatedDttm = new java.util.Date();
	}
}
