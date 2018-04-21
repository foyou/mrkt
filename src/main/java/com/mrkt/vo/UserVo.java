package com.mrkt.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName	UserVo
 * @Description 用户的卖家/买家身份vo类
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/19 13:47:41
 */
public class UserVo implements Serializable {

	private static final long serialVersionUID = -3094060248268730573L;

	/** 用户id */
	private Long uid;
	
	/** 用户名 */
	private String nName;
	
	/** 信誉平均分 */
	private Double avgScore = 0d;
	
	private List<UserCommentVo> commentVoList;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getnName() {
		return nName;
	}

	public void setnName(String nName) {
		this.nName = nName;
	}

	public Double getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(Double avgScore) {
		this.avgScore = avgScore;
	}

	public List<UserCommentVo> getCommentVoList() {
		return commentVoList;
	}

	public void setCommentVoList(List<UserCommentVo> commentVoList) {
		this.commentVoList = commentVoList;
	}
	
}
