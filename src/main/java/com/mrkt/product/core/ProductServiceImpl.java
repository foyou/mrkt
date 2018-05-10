package com.mrkt.product.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.mrkt.constant.ProductStatusEnum;
import com.mrkt.constant.ResultEnum;
import com.mrkt.exception.MrktException;
import com.mrkt.product.dao.CategoryRepository;
import com.mrkt.product.dao.OrderRepository;
import com.mrkt.product.dao.ProductRepository;
import com.mrkt.product.model.Comment;
import com.mrkt.product.model.Product;
import com.mrkt.usr.ThisUser;
import com.mrkt.usr.dao.UserRepository;
import com.mrkt.usr.model.UserBase;
import com.mrkt.vo.PreMessageVo;

@Service(value="productService")
public class ProductServiceImpl implements ProductService {
	
	/** 日志 */
	private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private OrderRepository orderRepository;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate redisTemplate;
	
	@SuppressWarnings("unchecked")
	@Override
	public Product findOne(Long id) throws Exception {
		Product entity = productRepository.findOne(id);
		if (entity == null) {
			logger.error("【查询商品详情】 找不到商品，productId={}", id);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		
		// 查询一次具体商品详情浏览量加1
		entity.setViews(entity.getViews()+1);
		entity = productRepository.save(entity);
		UserBase currUser = null;
		if ((currUser = ThisUser.get()) != null) {
			entity.setIsLike(redisTemplate.boundSetOps("pro_like_" + id).
						isMember(currUser.getUid()));
			entity.setIsColl(redisTemplate.boundSetOps("pro_coll_" + id).
						isMember(currUser.getUid()));
		}
		return entity;
	}
	
	@Override
	@Transactional
	public void saveOrUpdate(Product entity) throws Exception{
		if (entity.getId() != null && entity.getId() >= 0) {   // 更新商品信息
			Product po = productRepository.findOne(entity.getId());
			if (po == null) {
				logger.error("【更新商品】 找不到商品，productId={}", entity.getId());
				throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
			}
			
			if (!StringUtils.isEmpty(entity.getName())) po.setName(entity.getName());
			if (!StringUtils.isEmpty(entity.getDesc())) po.setDesc(entity.getDesc());
			if (!StringUtils.isEmpty(entity.getPrice())) po.setPrice(entity.getPrice());
			if (!StringUtils.isEmpty(entity.getCatId()) && !po.getCatId().equals(entity.getCatId())) {
				po.setCatId(entity.getCatId());
				po.setPtype(categoryRepository.getOne(entity.getCatId()).getName());// 更新冗余字段数据
			}
			if (!StringUtils.isEmpty(entity.getCount())) po.setCount(entity.getCount());
			if (!CollectionUtils.isEmpty(entity.getImages()))
				po.setImages(entity.getImages());
			
			entity = po;
			entity.setTmUpdated(new Date());
			
		} else {
			// 发布商品，初始化基本信息
			entity.setTmCreated(new Date());
			entity.setPtype(categoryRepository.getOne(entity.getCatId()).getName());// 设置商品分类的具体名
			entity.setMrktUser(userRepository.findOne(entity.getMrktUser().getUid()));
		}
		productRepository.save(entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Page<Product> findPage(int currPage, Long catId, String orderWay, String keywords) throws Exception {
		if (currPage < 0) {
			logger.error("【查询商品列表】 查询参数不正确，currPage={}", currPage);
			throw new MrktException(ResultEnum.PARAM_ERROR);
		}
		
		final int pageSize = 10;
		
		Specification<Product> sp = (root, query, builder) -> {
				/*
	             * 构造断言
	             * @param root 实体对象引用
	             * @param query 规则查询对象
	             * @param builder 规则构建对象
	             * @return 断言
	             */
				List<Predicate> predicates = new ArrayList<>();
				predicates.add(builder.equal(root.get("state").as(Integer.class), ProductStatusEnum.ON_SALE.getCode()));// 状态为1的商品，表示售卖中
				if (catId != null) {
					predicates.add(builder.equal(root.get("catId").as(Long.class), catId));
				}
				if (keywords != null && keywords.trim().length() > 0) {
					predicates.add(builder.like(root.get("name").as(String.class), "%" + keywords.trim() + "%"));
				}
				return builder.and(predicates.toArray(new Predicate[predicates.size()]));
			};
		if (orderWay == null || orderWay.trim().length() <= 0) orderWay = "tmCreated";// 默认为最新排序
		Pageable pageable = new PageRequest(currPage, pageSize, new Sort(new Order(Direction.DESC, orderWay)));
		Page<Product> page = productRepository.findAll(sp, pageable);
		// 处理当前用户对于商品的点赞情况和收藏情况
		UserBase currUser = null;
		if ((currUser = ThisUser.get()) != null)
			for (Product product : page) {
				product.setIsLike(redisTemplate.boundSetOps("pro_like_" + product.getId()).
							isMember(currUser.getUid()));
				product.setIsColl(redisTemplate.boundSetOps("pro_coll_" + product.getId()).
							isMember(currUser.getUid()));
			}
		
		return page;
	}

	@Override
	public void cancel(Long id) throws Exception {
		Product entity = productRepository.findOne(id);
		if (entity == null) {
			logger.error("【下架商品】 找不到商品，productId={}", id);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		
		entity.setState(ProductStatusEnum.BE_OFF.getCode());// 下架商品，将商品状态设为0
		productRepository.save(entity);
	}

	@Override
	public void cancelAll(Long[] ids) throws Exception {
		for (Long id : ids) {
			this.cancel(id);
		}
	}

	@Override
	public void delete(Long id) throws Exception {
		productRepository.delete(id);
	}

	@Override
	public void deleteAll(Long[] ids) throws Exception {
		for (Long id : ids) {
			this.delete(id);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void addLikes(Long id) throws Exception {
		Long result = redisTemplate.boundSetOps("pro_like_" + id).add(ThisUser.get().getUid());
		
		if (result == null || result == 0) {
			logger.error("【点赞商品】 用户重复点赞，uid={}, productId={}", ThisUser.get().getUid(), id);
			throw new MrktException(ResultEnum.USER_ADDLIKE_ERROR);
		}
		
//		redisTemplate.boundSetOps("pro_like_" + id).expire(100, TimeUnit.DAYS);
		Product entity = productRepository.findOne(id);
		if (entity == null) {
			logger.error("【点赞商品】 找不到商品，productId={}", id);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		
		// 点赞数加1
		entity.setLikes(entity.getLikes() + 1);
		productRepository.saveAndFlush(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void removeLikes(Long id) throws Exception {
		Long result = redisTemplate.boundSetOps("pro_like_" + id).remove(
				ThisUser.get().getUid());
		if (result == null || result == 0) {
			logger.error("【取消点赞】 用户没有点赞过该商品，uid={}, productId={}", ThisUser.get().getUid(), id);
			throw new MrktException(ResultEnum.USER_CANCELLIKE_ERROR);
		}
		
		Product entity = productRepository.findOne(id);
		if (entity == null) {
			logger.error("【取消点赞】 找不到商品，productId={}", id);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		entity.setLikes(entity.getLikes() - 1);
		productRepository.saveAndFlush(entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void addCollection(Long id) throws Exception {
		// 用户 收藏 商品，多对多，换成两个set存储在redis中
		Long result = redisTemplate.boundSetOps("pro_coll_" + id).add(
				ThisUser.get().getUid());
		redisTemplate.boundSetOps("user_coll_" + ThisUser.get().getUid()).add(id);
		
		if (result == null || result == 0) {
			logger.error("【收藏商品】 用户重复收藏该商品，uid={}, productId={}", ThisUser.get().getUid(), id);
			throw new MrktException(ResultEnum.USER_ADDCOLL_ERROR);
		}
		
//		redisTemplate.boundSetOps("pro_coll_" + id).expire(100, TimeUnit.DAYS);
//		redisTemplate.boundSetOps("user_coll_" + ThisUser.get().getUid()).expire(100, TimeUnit.DAYS);
		
		Product entity = productRepository.findOne(id);
		if (entity == null) {
			logger.error("【收藏商品】 找不到商品，productId={}", id);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		
		// 收藏数加1
		entity.setCollection(entity.getCollection() + 1);
		productRepository.saveAndFlush(entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void removeCollection(Long id) throws Exception {
		// 用户 收藏 商品，多对多，换成两个set存储在redis中
		Long result = redisTemplate.boundSetOps("pro_coll_" + id).remove(ThisUser.get().getUid());
		redisTemplate.boundSetOps("user_coll_" + ThisUser.get().getUid()).remove(id);
		
		if (result == null || result == 0) {
			logger.error("【取消收藏】 用户没有收藏过该商品，uid={}, productId={}", ThisUser.get().getUid(), id);
			throw new MrktException(ResultEnum.USER_CANCELCOLL_ERROR);
		}
		Product entity = productRepository.findOne(id);
		if (entity == null) {
			logger.error("【取消收藏】 找不到商品，productId={}", id);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		
		entity.setCollection(entity.getCollection() - 1);
		productRepository.saveAndFlush(entity);
	}
	
	@Override
	public Product addComment(Long productId, String commentContent) throws Exception {
		Product originalProduct = productRepository.findOne(productId);
		if (originalProduct == null) {
			logger.error("【评论商品】 找不到商品，productId={}", productId);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		
		UserBase UserBase = ThisUser.get();
		Comment comment = new Comment(UserBase, commentContent);
		originalProduct.addComment(comment);
		return productRepository.save(originalProduct);
	}

	@Override
	public void removeComment(Long productId, Long commentId) throws Exception {
		Product originalProduct = productRepository.findOne(productId);
		if (originalProduct == null) {
			logger.error("【删除评论】 找不到商品，productId={}", productId);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		
		originalProduct.removeComment(commentId);
		productRepository.save(originalProduct);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<Product> getMine() throws Exception {
		Specification<Product> sp = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("state").as(Integer.class), ProductStatusEnum.ON_SALE.getCode()));
			predicates.add(builder.equal(root.get("mrktUser").as(UserBase.class), ThisUser.get()));
			// 排序
			query.orderBy(builder.desc(root.get("tmCreated").as(Date.class)));
			
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		List<Product> products = productRepository.findAll(sp);
		// 处理当前用户对于商品的点赞情况和收藏情况
		final UserBase currUser = ThisUser.get();
		if (currUser != null && !CollectionUtils.isEmpty(products))
			products.forEach(product -> {
				product.setIsLike(
						redisTemplate.boundSetOps("pro_like_" + product.getId()).isMember(currUser.getUid()));
				product.setIsColl(true);
			});
		return products;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> getCollection() throws Exception {
		Set<Integer> idsInt = redisTemplate.boundSetOps("user_coll_" + ThisUser.get().getUid()).members();
		if (!CollectionUtils.isEmpty(idsInt)) {
//			redisTemplate.boundSetOps("user_coll_" + ThisUser.get().getUid()).expire(100, TimeUnit.DAYS);
			
			Set<Long> ids = idsInt.stream()
					.map(e -> new Long(e))
					.collect(Collectors.toSet());
			
			List<Product> products = productRepository.findAll(ids);
			if (products == null) {
				logger.error("【我收藏的】 找不到商品，productIds={}", ids);
				throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
			}
			// 处理当前用户对于商品的点赞情况和收藏情况
			final UserBase currUser = ThisUser.get();
			if (currUser != null)
				products.forEach(product -> {
					product.setIsLike(
							redisTemplate.boundSetOps("pro_like_" + product.getId()).isMember(currUser.getUid()));
					product.setIsColl(true);
				});
			return products;
		} else {
			return null;
		}
	}

	@Override
	public List<PreMessageVo> getPreMessage(Long productId) throws Exception {
		// 先判断该商品是否属于此用户
		Product product = productRepository.findOne(productId);
		if (product == null) {
			logger.error("【查询预定留言】 找不到商品，productId={}", productId);
			throw new MrktException(ResultEnum.PRODUCT_NOT_EXIST);
		}
		if (!ThisUser.get().getUid().equals(product.getMrktUser().getUid())) {
			logger.error("【查询预定留言】 当前用户不是商品卖家，uid={}, productId={}", ThisUser.get().getUid(), productId);
			throw new MrktException(ResultEnum.USER_NOT_SELLER);
		}
		
		List<com.mrkt.product.model.Order> orderList = orderRepository.findByProductId(productId);
		List<PreMessageVo> preMessageVoList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(orderList)) {
			preMessageVoList = orderList.stream().map(e ->
				new PreMessageVo(e.getProduct().getId(), e.getBuyerId(), e.getBuyerName(), 
						e.getSellerId(), e.getMessage(), e.getId())
			).collect(Collectors.toList());
		}
		return preMessageVoList;
	}

}
