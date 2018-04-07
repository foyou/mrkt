package com.mrkt.exception;

import com.mrkt.constant.ResultEnum;

/**
 * @ClassName	MrktException
 * @Description
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/06 23:40:53
 */
public class MrktException extends RuntimeException {
	private static final long serialVersionUID = 3728512423959257351L;
	private Integer code;

    public MrktException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public MrktException(Integer code, String message) {
        super(message);
        this.code = code;
    }

	public Integer getCode() {
		return code;
	}
    
}
