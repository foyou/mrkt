package com.mrkt.usr.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mrkt.authorization.annotation.Authorization;
import com.mrkt.usr.core.UserServiceImpl;
import com.mrkt.vo.ReturnModel;

/**
 * @ClassName	UserController
 * @Description 顾客的控制层
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/03/06 22:26:22
 */
@RestController
@RequestMapping("/customer")
public class UserController {

	@Autowired
	private UserServiceImpl userService;
	
	/**
	 * 查看顾客资料
	 * @param uid
	 * @return
	 */
	@Authorization
	@RequestMapping(value="/{uid}", method=RequestMethod.GET)
	public ReturnModel getUser(@PathVariable("uid") Long uid) {
		return ReturnModel.SUCCESS(userService.get(uid));
	}
	
	/**
	 * 修改顾客信息
	 * @param uid
	 * @return
	 */
	@Authorization
	@RequestMapping(value="/{uid}", method=RequestMethod.PUT)
	public ReturnModel editUser(@PathVariable("uid") Long uid) {
		// 修改 TODO
		
		return ReturnModel.SUCCESS(userService.get(uid));
	}
	
	/**
	 * 获取用户买家身份信息
	 * @param uid
	 * @return
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/{uid}/buyer", method=RequestMethod.GET)
	public ReturnModel getUserBuyerInfo(@PathVariable("uid") Long uid) throws Exception {
		
		return ReturnModel.SUCCESS(userService.getUserBuyerInfo(uid));
	}
	
	/**
	 * 获取用户卖家身份信息
	 * @param uid
	 * @return
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/{uid}/seller", method=RequestMethod.GET)
	public ReturnModel getUserSellerInfo(@PathVariable("uid") Long uid) throws Exception {
		
		return ReturnModel.SUCCESS(userService.getUserSellerInfo(uid));
	}
	
}
