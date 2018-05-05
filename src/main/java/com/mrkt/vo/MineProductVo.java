package com.mrkt.vo;

import java.util.Date;

/**
 * @ClassName	MineProductVo
 * @Description 我发布的商品vo类
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/05/03 14:29:23
 */
public class MineProductVo extends BaseProductVo {

	private static final long serialVersionUID = 6738871230744684565L;

	/** 浏览量 */
	private Integer views;
	
	/** 发布时间 */
	private Date time;

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
