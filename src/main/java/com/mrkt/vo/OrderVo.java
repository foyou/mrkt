package com.mrkt.vo;

import java.io.Serializable;

/**
 * @ClassName	OrderVo
 * @Description 完善订单详情时的订单vo类
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/05/03 14:50:56
 */
public class OrderVo implements Serializable {

	private static final long serialVersionUID = 6531733273104371929L;

	private String buyer_name;
	
	private String address;
	
	private String phone;
	
	private String buyer_wx;

	public String getBuyer_name() {
		return buyer_name;
	}

	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBuyer_wx() {
		return buyer_wx;
	}

	public void setBuyer_wx(String buyer_wx) {
		this.buyer_wx = buyer_wx;
	}
	
}
