package com.mrkt.usr.core;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.mrkt.constant.OrderStatusEnum;
import com.mrkt.constant.ResultEnum;
import com.mrkt.exception.MrktException;
import com.mrkt.product.core.OrderService;
import com.mrkt.product.model.Order;
import com.mrkt.usr.dao.UserRepository;
import com.mrkt.usr.model.UserBase;
import com.mrkt.vo.UserCommentVo;
import com.mrkt.vo.UserVo;

@Service
public class UserServiceImpl {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderService orderService;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate redisTemplates;
	
	private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@SuppressWarnings("unchecked")
	public UserBase get(Long uid){
		UserBase user = null;
		if (( user = (UserBase) redisTemplates.opsForHash().get("user", uid) ) != null){
			//nothing
			logger.debug("Cache hit, user: " + user);
		}else{
			if ( (user = userRepository.findOne(uid)) != null ) {
				logger.debug("Get from DB, user: " + user);
				redisTemplates.opsForHash().put("user", user.getUid(), user);
			}
		}
		return user;
	}
	
	@SuppressWarnings("unchecked")
	public UserBase save(UserBase user){
		user = userRepository.saveAndFlush(user);
		redisTemplates.opsForHash().put("user", user.getUid(), user);
		return user;
	}
	
	public UserVo getUserSellerInfo(Long uid) throws Exception {
		UserVo userVo = new UserVo();
		
		UserBase user = get(uid);
		if (user == null) {
			logger.error("【获取用户卖家信息】 查询不到此用户，uid={}", uid);
			throw new MrktException(ResultEnum.USER_NOT_EXIT);
		}
		
		userVo.setUid(uid);
		userVo.setnName(user.getnName());
		
		List<Order> orderList = orderService.findByState(OrderStatusEnum.BE_COMPLETE.getCode(), uid, "sellerId");
		if (!CollectionUtils.isEmpty(orderList)) {
			List<UserCommentVo> commentVoList = orderList.stream()
					.map(e -> new UserCommentVo(e.getSellerScore(), e.getSellerComment()))
					.collect(Collectors.toList());
			userVo.setCommentVoList(commentVoList);
			// 算平均分
			Double avgScore = commentVoList.stream()
					.collect(Collectors.averagingInt(UserCommentVo::getScore));
			userVo.setAvgScore(avgScore);
		}
		return userVo;
	}
	
	
	public UserVo getUserBuyerInfo(Long uid) throws Exception {
		UserVo userVo = new UserVo();
		
		UserBase user = get(uid);
		if (user == null) {
			logger.error("【获取用户买家信息】 查询不到此用户，uid={}", uid);
			throw new MrktException(ResultEnum.USER_NOT_EXIT);
		}
		
		userVo.setUid(uid);
		userVo.setnName(user.getnName());
		
		List<Order> orderList = orderService.findByState(OrderStatusEnum.BE_COMPLETE.getCode(), uid, "buyerId");
		if (!CollectionUtils.isEmpty(orderList)) {
			List<UserCommentVo> commentVoList = orderList.stream()
					.map(e -> new UserCommentVo(e.getSellerScore(), e.getSellerComment()))
					.collect(Collectors.toList());
			userVo.setCommentVoList(commentVoList);
			// 算平均分
			Double avgScore = commentVoList.stream()
					.collect(Collectors.averagingInt(UserCommentVo::getScore));
			userVo.setAvgScore(avgScore);
		}
		return userVo;
	}
}
