package com.mrkt.vo;

import java.util.Date;

/**
 * @ClassName	SellProductVo
 * @Description 卖出的商品
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/19 20:07:22
 */
public class SellProductVo extends BaseProductVo {

	private static final long serialVersionUID = 3879434992507603242L;

	/** 售出时间 */
	private Date time;
	
	/** 买家id */
	private Long buyerId;
	
	/** 订单id */
	private String orderId;

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
