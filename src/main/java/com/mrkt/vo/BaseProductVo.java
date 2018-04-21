package com.mrkt.vo;

import java.io.Serializable;

/**
 * @ClassName	BaseProductVo
 * @Description 预定商品、购买的商品、出售的商品、收藏的商品
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/19 19:44:56
 */
public class BaseProductVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 商品名 */
	protected String name;
	
	/** 商品id */
	protected Long productId;
	
	/** 图片链接 */
	protected String pic;
	
	/** 价格  */
	protected Double price;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}


	
}
