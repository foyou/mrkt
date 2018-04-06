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
	;
	
    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

	public String getMsg() {
		return msg;
	}

	public Integer getCode() {
		return code;
	}
	
}
