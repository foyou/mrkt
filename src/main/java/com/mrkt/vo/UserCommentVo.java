package com.mrkt.vo;

import java.io.Serializable;

/**
 * @ClassName	UserCommentVo
 * @Description 买家/卖家身份评价
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/19 15:52:11
 */
public class UserCommentVo implements Serializable {

	private static final long serialVersionUID = 9068027402717642571L;

	/** 评分 */
	private Integer score;
	
	/** 评语 */
	private String comment;
	
	

	public UserCommentVo(Integer score, String comment) {
		super();
		this.score = score;
		this.comment = comment;
	}

	public UserCommentVo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
