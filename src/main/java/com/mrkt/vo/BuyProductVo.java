package com.mrkt.vo;

import java.util.Date;

/**
 * @ClassName	BuyProductVo
 * @Description 购买的商品
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/19 20:00:06
 */
public class BuyProductVo extends BaseProductVo {

	private static final long serialVersionUID = 1L;

	/** 购买时间 */
	private Date time;
	
	/** 卖家id */
	private Long sellerId;
	
	/** 订单id */
	private String orderId;

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
