package com.mrkt.product.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mrkt.usr.model.UserBase;

/**
 * 商品留言 实体
 */
@Entity // 实体
@Table(name="mrkt_pro_comment")
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	@Column(name="comment_id")
	private Long id; // 用户的唯一标识

	@NotEmpty(message = "留言内容不能为空")
	@Size(min=2, max=255)
	@Column(nullable = false) // 映射为字段，值不能为空
	private String content;
 
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="uid")
	@JsonIgnore
	private UserBase UserBase;
	
	@Column(nullable = false) // 映射为字段，值不能为空
	@org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
	private Timestamp createTime;
	
	/** 昵称 */
	@Transient
	private String nName;
	
	/** 微信头像 */
	@Transient
	private String avatar;
	
	@Transient
	/** 冗余字段，标志评论是否属于当前用户 */
	private Boolean belongCurrUser = false;
 
	protected Comment() {
		// TODO Auto-generated constructor stub
	}
	public Comment(UserBase UserBase, String content) {
		this.content = content;
		this.UserBase = UserBase;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public UserBase getUserBase() {
		return UserBase;
	}
	public void setUserBase(UserBase userBase) {
		UserBase = userBase;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getnName() {
		return nName;
	}
	public void setnName(String nName) {
		this.nName = nName;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Boolean getBelongCurrUser() {
		return belongCurrUser;
	}
	public void setBelongCurrUser(Boolean belongCurrUser) {
		this.belongCurrUser = belongCurrUser;
	}
	
	@Override
	public String toString() {
		return "Comment [id=" + id + ", content=" + content + ", UserBase=" + UserBase + ", createTime=" + createTime
				+ ", nName=" + nName + ", avatar=" + avatar + ", belongCurrUser=" + belongCurrUser + "]";
	}

	
}
