package com.mrkt.vo;

/**
 * @ClassName	CollProductVo
 * @Description 收藏的商品VO类
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/19 20:04:33
 */
public class CollProductVo extends BaseProductVo {

	private static final long serialVersionUID = 1L;
	
	/** 卖家id */
	private Long sellerId;
	
	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

}
