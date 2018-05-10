package com.mrkt.vo;

import java.util.Date;

/**
 * @ClassName	OrderProductVo
 * @Description 预定商品的订单VO类
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/19 19:57:08
 */
public class OrderProductVo extends BaseProductVo {

	private static final long serialVersionUID = 1L;

	/** 预定时间 */
	private Date time;
	
	/** 卖家id */
	private Long sellerId;
	
	/** 预定留言 */
	private String preMessage;

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

	public String getPreMessage() {
		return preMessage;
	}

	public void setPreMessage(String preMessage) {
		this.preMessage = preMessage;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
