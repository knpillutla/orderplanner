package com.example.order.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Entity
@Data
@Table(name="ORDER_LINES")
public class OrderLine  implements Serializable{
	@Column(name="ID")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="ORDER_ID", nullable=false)
    private Order order;

	@Column(name="LINE_NBR")
	Integer orderLineNbr;

	@Column(name="LOCN_NBR")
	Integer locnNbr;

	@Column(name="ITEM_BRCD")
	String itemBrcd;

	@Column(name="ORIG_ORDER_QTY")
	Integer origOrderQty;

	@Column(name="ORDER_QTY")
	Integer orderQty;

	@Column(name="CANCELLED_QTY")
	Integer cancelledQty;

	@Column(name="SHORT_QTY")
	Integer shortQty;

	@Column(name="PICKED_QTY")
	Integer pickedQty;

	@Column(name="PACKED_QTY")
	Integer packedQty;

	@Column(name="SHIPPED_QTY")
	Integer shippedQty;

	@Column(name="STAT_CODE")
	Integer statCode;

	@Column(name="ORIG_PACKAGE_NBR")
	String origPackageNbr;
	
	@Column(name="PACKAGE_NBR")
	String packageNbr;

	@Column(name="ITEM_WIDTH")
    double itemWidth;
    
    @Column(name="ITEM_HEIGHT")
    double itemHeight;
    
    @Column(name="ITEM_LENGTH")
    double itemLength;
    
    @Column(name="ITEM_UNIT_WT")
    double itemUnitWt;
    
    @Column(name="ITEM_UNIT_VOL")
    double itemUnitVol;

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

	public OrderLine(Integer locnNbr, Integer orderLineNbr, String itemBrcd, Integer origOrderQty,
			Integer orderQty, String source, String transactionName,
			String refField1, String refField2, String userId) {
		this.locnNbr = locnNbr;
		this.orderLineNbr = orderLineNbr;
		this.itemBrcd = itemBrcd;
		this.origOrderQty = origOrderQty;
		this.orderQty = orderQty;
		this.source = source;
		this.transactionName = transactionName;
		this.refField1 = refField1;
		this.refField2 = refField2;
		this.createdDttm = new java.util.Date();
		this.updatedDttm = new java.util.Date();
		this.createdBy = userId;
		this.updatedBy = userId;
	}
}
