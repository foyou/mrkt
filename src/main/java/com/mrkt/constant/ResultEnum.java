package com.mrkt.constant;

/**
 * @ClassName	ResultEnum
 * @Description
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/06 23:43:26
 */
public enum ResultEnum {

	PARAM_ERROR(1, "参数不正确"),
	
	PRODUCT_NOT_EXIST(10, "商品不存在错误"),
	
	USER_NOT_SELLER(11, "当前用户不是商品卖家错误"), 
	
	USER_ADDLIKE_ERROR(12, "用户重复点赞错误"), 
	
	USER_CANCELLIKE_ERROR(13, "用户取消点赞错误"), 
	
	COMMENT_NOT_EXIST(14, "评论不存在"), 
	
	PRODUCT_STATUS_ERROR(15, "购买的商品状态异常"), 
	
	BUYER_IS_SELLER_ERROR(16, "买家即卖家错误"), 

	ORDER_STATUS_ERROR(17, "订单状态错误"), 
	
	ORDER_NOT_EXIST(18, "订单不存在错误"), 
	
	USER_ADDCOLL_ERROR(19, "用户重复收藏商品错误"),
	
	USER_CANCELCOLL_ERROR(20, "用户取消收藏错误"),
	
	UPLOAD_PICTRUE_FIAL(21, "图片上传失败"),
	
	;
	
	/** 状态码 */
    private Integer code;
    /** 状态信息 */
    private String message;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

	public String getMessage() {
		return message;
	}

	public Integer getCode() {
		return code;
	}
	
}
