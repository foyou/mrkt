package com.mrkt.constant;

/**
 * @ClassName	ProductStatusEnum
 * @Description 商品状态常量枚举类<br>
 * 				商品状态，0下架；1售卖中；2被预定；3已售出； 
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/03/29 14:03:35
 */
public enum ProductStatusEnum {

	BE_OFF(0, "已下架"),
	ON_SALE(1, "售卖中"),
	BE_ORDERED(2, "被预定"),
	BE_SOLD(3, "已售出");
	
	/** 状态码 */
	private int code;
	
	/** 状态信息 */
	private String message;
	
	ProductStatusEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
}
