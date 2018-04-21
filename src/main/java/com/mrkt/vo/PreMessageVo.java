package com.mrkt.vo;

import java.io.Serializable;

/**
 * @ClassName	PreMessageVo
 * @Description 预定留言
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/06 23:24:58
 */
public class PreMessageVo implements Serializable {

	private static final long serialVersionUID = -4213674140956057937L;

	//要买的商品主键
	private Long productId;
	
	//买家主键
	private Long buyerId;
	
	//买家姓名
	private String buyerName;
	
	//卖家主键
	private Long sellerId;
	
	//预定留言
	private String message;
	
	//关联生成的订单
	private String orderId;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public PreMessageVo() {
		super();
	}

	public PreMessageVo(Long productId, Long buyerId, String buyerName, Long sellerId, String message,
			String orderId) {
		super();
		this.productId = productId;
		this.buyerId = buyerId;
		this.buyerName = buyerName;
		this.sellerId = sellerId;
		this.message = message;
		this.orderId = orderId;
	}
	
}
