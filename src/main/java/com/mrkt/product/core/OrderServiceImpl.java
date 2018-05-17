package com.mrkt.product.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.mrkt.constant.OrderStatusEnum;
import com.mrkt.constant.ProductStatusEnum;
import com.mrkt.constant.ResultEnum;
import com.mrkt.exception.MrktException;
import com.mrkt.product.dao.OrderRepository;
import com.mrkt.product.dao.ProductRepository;
import com.mrkt.product.model.Order;
import com.mrkt.product.model.Product;
import com.mrkt.usr.ThisUser;

/**
 * @ClassName	OrderServiceImpl
 * @Description 订单
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/02/25 13:29:45
 */
@Service(value="orderService")
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductRepository productRepository;
	
	private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Override
	@Transactional
	public boolean requestOrder(Order order, Long productId) throws Exception {
		Product product = productRepository.findOne(productId);
		if (product == null) {
			logger.error("【预定留言】 商品不存在，productId={}", productId);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
			
		} else if (product.getState() != ProductStatusEnum.ON_SALE.getCode()) {
			logger.error("【预定留言】 商品状态异常，product={}", product);
			throw new MrktException(ResultEnum.PRODUCT_STATUS_ERROR);
			
		} else if (ThisUser.get().getUid().equals(product.getMrktUser().getUid())) {
			logger.error("【预定留言】 买家即卖家错误，productId={}, buyerId={}", productId, ThisUser.get().getUid());
			throw new MrktException(ResultEnum.BUYER_IS_SELLER_ERROR);
		}
		
//		product.setState(ProductState.BE_ORDERED.getState());// 2表示商品被预定
		order.setProduct(product);
		order.setAmount(product.getPrice());
		// 设置卖家信息
		order.setSellerId(product.getMrktUser().getUid());
		order.setSellerName(product.getMrktUser().getnName());
		// 设置买家信息
		order.setBuyerId(ThisUser.get().getUid());
		order.setBuyerName(ThisUser.get().getnName());
		
//		productRepository.saveAndFlush(product);
		return orderRepository.saveAndFlush(order) != null;
	}

	@Override
	@Transactional
	public boolean processOrder(String id, int state) throws Exception {
		Order order = orderRepository.findByIdAndSellerId(id, ThisUser.get().getUid());
		if (order == null) {
			logger.error("【卖家处理订单】 订单不存在，orderId={}, sellerId={}", id, ThisUser.get().getUid());
			throw new MrktException(ResultEnum.ORDER_NOT_EXIST);
		}
		
		if (state == OrderStatusEnum.BE_CANCELED.getCode()) {
			// 卖家取消订单或拒绝预定，无操作
		} else if (state == OrderStatusEnum.BE_WAITING_PYAMENT.getCode()) {
			// 卖家接受预定请求，修改商品状态为被预定，同时拒绝其它预定
			// 修改商品状态
			Product product = order.getProduct();
			product.setState(ProductStatusEnum.BE_ORDERED.getCode());
			productRepository.save(product);
			// 需要拒绝其它请求
			List<Order> orders = orderRepository.findByProductId(product.getId());
			if (!CollectionUtils.isEmpty(orders)) {
				orders.forEach(o -> {
					if (!o.getId().equals(id)) {
						o.setState(OrderStatusEnum.BE_CANCELED.getCode());
						orderRepository.save(order);
					}
				});
			}
		}
		// 修改订单状态
		order.setState(state);
		orderRepository.saveAndFlush(order);
		return true;
	}

	@Override
	@Transactional
	public boolean submitOrder(Order order) throws Exception{
		Order entity = orderRepository.findByIdAndBuyerId(order.getId(), ThisUser.get().getUid());
		
		if (entity == null) {
			logger.error("【买家完善订单信息】 订单不存在，orderId={}, buyerId={}", order.getId(), ThisUser.get().getUid());
			throw new MrktException(ResultEnum.ORDER_NOT_EXIST);
			
		} else if (entity.getProduct().getState() != ProductStatusEnum.BE_ORDERED.getCode()) {
			logger.error("【买家完善订单信息】 商品状态异常，商品没有处于被预定状态，product={}", entity.getProduct());
			throw new MrktException(ResultEnum.PRODUCT_STATUS_ERROR);
			
		} else if (!entity.getState().equals(OrderStatusEnum.BE_WAITING_PYAMENT.getCode())) {
			logger.error("【买家完善订单信息】 订单状态错误，不处于待支付状态不允许完善订单信息，order={}", entity);
			throw new MrktException(ResultEnum.ORDER_STATUS_ERROR);
		}
		
		if (!StringUtils.isEmpty(order.getBuyerName())) {
			entity.setBuyerName(order.getBuyerName());
		}
		entity.setBuyerPhone(order.getBuyerPhone());
		entity.setBuyerWx(order.getBuyerWx());
		entity.setAddress(order.getAddress());
		entity.setState(OrderStatusEnum.BE_WAITING_RECEIVING.getCode());// 3表示订单状态变为待收货
		entity.setCreateTime(new Date());
		
		return orderRepository.saveAndFlush(entity) != null;
	}

	@Override
	@Transactional
	public boolean endOrder(String id) throws Exception {
		Order order = orderRepository.findByIdAndBuyerId(id, ThisUser.get().getUid());
		if (order == null) {
			logger.error("【买家确定收货】 订单不存在，orderId={}, buyerId={}", id, ThisUser.get().getUid());
			throw new MrktException(ResultEnum.ORDER_NOT_EXIST);
		}
		
		Product product = order.getProduct();
		if (product.getState() != ProductStatusEnum.BE_ORDERED.getCode()) {
			logger.error("【买家确定收货】 商品状态异常，商品没有处于被预定状态，product={}", product);
			throw new MrktException(ResultEnum.PRODUCT_STATUS_ERROR);
			
		} else if (order.getState() != OrderStatusEnum.BE_WAITING_RECEIVING.getCode()) {// 不处于被预定的商品或不处于待收货状态的订单
			logger.error("【买家完善订单信息】 订单状态错误，不处于待收货状态的订单不允许确定收货，order={}", order);
			throw new MrktException(ResultEnum.ORDER_STATUS_ERROR);
			
		}
		product.setState(ProductStatusEnum.BE_SOLD.getCode());// 商品3已售出
		order.setState(OrderStatusEnum.BE_COMMENTING.getCode());// 待评论
		order.setEndTime(new Date());
		
		return productRepository.saveAndFlush(product) != null &&
				orderRepository.saveAndFlush(order) != null;
	}

	@Override
	@Transactional
	public boolean commentSeller(String id, int score, String comment) throws Exception {
		Order order = orderRepository.findByIdAndBuyerId(id, ThisUser.get().getUid());
		if (order == null) {
			logger.error("【买家评价卖家】 订单不存在，orderId={}, buyerId={}", id, ThisUser.get().getUid());
			throw new MrktException(ResultEnum.ORDER_NOT_EXIST);
			
		} else if (order.getState() != OrderStatusEnum.BE_COMMENTING.getCode()) {
			logger.error("【买家评价卖家】 订单状态错误，未确定收货的订单不允许买卖双方互评，order={}", order);
			throw new MrktException(ResultEnum.ORDER_STATUS_ERROR);
			
		}
		order.setSellerScore(score);
		order.setSellerComment(comment);
		// 若双方都互评了，则修改订单状态为交易完成
		if (order.getBuyerComment() != null && !"".equals(order.getBuyerComment())) {
			order.setState(OrderStatusEnum.BE_COMPLETE.getCode());
		}
		
		return orderRepository.saveAndFlush(order) != null;
	}

	@Override
	@Transactional
	public boolean commentBuyer(String id, int score, String comment) throws Exception {
		Order order = orderRepository.findByIdAndSellerId(id, ThisUser.get().getUid());
		if (order == null) {
			logger.error("【卖家评价买家】 订单不存在，orderId={}, sellerId={}", id, ThisUser.get().getUid());
			throw new MrktException(ResultEnum.ORDER_NOT_EXIST);
			
		} else if (order.getState() != OrderStatusEnum.BE_COMMENTING.getCode()) {
			logger.error("【卖家评价买家】 订单状态错误，未确定收货的订单不允许买卖双方互评，order={}", order);
			throw new MrktException(ResultEnum.ORDER_STATUS_ERROR);
			
		}
		
		order.setBuyerScore(score);
		order.setBuyerComment(comment);
		// 若双方都互评了，则修改订单状态为交易完成
		if (order.getSellerComment() != null && !"".equals(order.getSellerComment())) {
			order.setState(OrderStatusEnum.BE_COMPLETE.getCode());
		}
		orderRepository.saveAndFlush(order);
		return true;
	}

	@Override
	public Order findOne(String id) throws Exception {
		Order result = orderRepository.findOne(id);
		if (result != null) {
			result.setPId(result.getProduct().getId());
		}
		return result;
	}

	@Override
	public List<Order> findByStateAsBuyer(int stateBegin, int stateEnd) throws Exception {
		Specification<Order> sp = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.between(root.get("state").as(Integer.class), stateBegin, stateEnd));
			predicates.add(builder.equal(root.get("buyerId").as(Long.class), ThisUser.get().getUid()));
			// 排序
			query.orderBy(builder.desc(root.get("updateTime").as(Date.class)));
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		return orderRepository.findAll(sp);
	}

	@Override
	public List<Order> findByStateAsSeller() throws Exception {
		Specification<Order> sp = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.between(root.get("state").as(Integer.class), 
					OrderStatusEnum.BE_COMMENTING.getCode(), OrderStatusEnum.BE_COMPLETE.getCode()));
//			predicates.add(builder.between(root.join("product").get("state").as(Integer.class), 
//					ProductStatusEnum.BE_ORDERED.getCode(), ProductStatusEnum.BE_SOLD.getCode()));
			predicates.add(builder.equal(root.get("sellerId").as(Long.class), ThisUser.get().getUid()));
			// 排序
			query.orderBy(builder.desc(root.get("updateTime").as(Date.class)));
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		return orderRepository.findAll(sp);
	}

	@Override
	@Transactional
	public boolean deleteOrder(String id) throws Exception {
		Order order = orderRepository.findByIdAndBuyerId(id, ThisUser.get().getUid());
		if (order == null) {
			logger.error("【买家删除订单】 订单不存在，orderId={}, buyerId={}", id, ThisUser.get().getUid());
			throw new MrktException(ResultEnum.ORDER_NOT_EXIST);
		}
		
		// 删除订单前处理商品状态，
		// 订单状态0|1， 2|3(商品状态：被预定)， 4|5(商品状态：已售出)
		// 删除订单状态为2|3的订单需要修改商品为“售卖中”
		Product product = order.getProduct();
		if (order.getState() == OrderStatusEnum.BE_WAITING_PYAMENT.getCode() ||
			order.getState() == OrderStatusEnum.BE_WAITING_RECEIVING.getCode()) {
			// 删除订单，商品还原回到售卖状态
			product.setState(ProductStatusEnum.ON_SALE.getCode());
			productRepository.save(product);
		}
		order.setState(OrderStatusEnum.BE_DELETED.getCode());
		return orderRepository.save(order) != null;
	}

	@Override
	public List<Order> findByState(Integer state, long uid, String buyerSeller) throws Exception {
		Specification<Order> sp = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("state").as(Integer.class), state));
			predicates.add(builder.equal(root.get(buyerSeller).as(Long.class), uid));
			// 排序
			query.orderBy(builder.desc(root.get("updateTime").as(Date.class)));
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		return orderRepository.findAll(sp);
	}

}
