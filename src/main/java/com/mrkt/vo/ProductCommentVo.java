package com.mrkt.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * @ClassName	ProductCommentVo
 * @Description 商品留言VO类
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/05/02 19:33:36
 */
public class ProductCommentVo implements Serializable {

	private static final long serialVersionUID = -6429537883065794480L;

	/** 被评论的商品id */
	@NotNull
	private Long productId;
	
	/** 商品留言内容 */
	@NotNull
	private String commentContent;
	
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	
}
