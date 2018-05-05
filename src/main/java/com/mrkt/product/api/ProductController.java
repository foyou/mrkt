package com.mrkt.product.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mrkt.authorization.annotation.Authorization;
import com.mrkt.config.CommonConfig;
import com.mrkt.product.core.ProductService;
import com.mrkt.product.model.Image;
import com.mrkt.product.model.Product;
import com.mrkt.usr.ThisUser;
import com.mrkt.utils.UploadUtil;
import com.mrkt.vo.CollProductVo;
import com.mrkt.vo.MineProductVo;
import com.mrkt.vo.ReturnModel;

/**
 * @ClassName	ProductCotroller	
 * @Description
 * @author		hdonghong
 * @version 	v1.0 
 * @since		2018/02/19 13:16:21
 */
@RestController
public class ProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private CommonConfig commonConfig;
	
	private final Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	/**
	 * 查询商品信息
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/{id}", method=RequestMethod.GET)
	public ReturnModel getProduct(@PathVariable("id") Long id) throws Exception {
		Product entity = productService.findOne(id);
		logger.info("thisUser: " + ThisUser.get());
		return ReturnModel.SUCCESS(entity);
	}
	
	/**
	 * 分页展示商品，搜索商品
	 * @param currPage 当前页码
	 * @param cat_id 商品类型编号
	 * @param orderWay tmCreated(按最新排序) or views(按浏览量排序)
	 * @param keywords 搜索的关键词
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/products/all", method=RequestMethod.GET)
	public ReturnModel getProducts(
			@RequestParam(value="curr_page", defaultValue="1") Integer currPage, 
			@RequestParam(value="catId", required=false) Long catId, 
			@RequestParam(value="order_way", required=false) String orderWay, 
			@RequestParam(value="keywords", required=false) String keywords) throws Exception {
		Page<Product> page = productService.findPage(currPage-1, catId, orderWay, keywords);
		return ReturnModel.SUCCESS(page);
	}
	
	/**
	 * 上架新商品
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products", method=RequestMethod.POST)
	public ReturnModel addProduct(@RequestBody Product entity, HttpServletRequest request) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n*******toString():"+ request.toString())
		  .append("\n*******getContentLength:" + request.getContentLength())
		  .append("\n*******getAttributeNames:" + request.getAttributeNames())
		  .append("\n*******getAuthType:" + request.getAuthType())
		  .append("\n*******Header:" + request.getHeader("Content-Type"))
		  .append("\n*******CharacterEncoding:" + request.getCharacterEncoding())
		  .append("\n*******name:" + request.getParameter("name"))
		  .append("\n*******price:" + request.getParameter("price"))
		  .append("\n*******count:" + request.getParameter("count"))
		  .append("\n*******Product:" + entity)
		  .append("\n*******ParameterMap is below:");
		for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet())
			sb.append("\n*******").append(e.getKey()).append(":").append(Arrays.toString(e.getValue()));
		logger.info(sb.toString());
		
		entity.setMrktUser(ThisUser.get());
		
		// 处理图片
		Set<Image> images = entity.getImages();
		if (images != null && images.size() > 0)
			for (Image image : images) {
				// 上传至七牛云服务器
				String path = UploadUtil.uploadImage(image.getPath(), commonConfig);
				if (path == null)
					logger.error("【发布商品】 图片上传失败，base64={}" + image.getPath());
				image.setPath(path);
			}
		productService.saveOrUpdate(entity);
		return ReturnModel.SUCCESS();
	}
	
	/**
	 * 修改商品信息
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/{id}", method=RequestMethod.PUT)
	public ReturnModel updateProduct(
			@PathVariable("id") Long id,
			@RequestBody Product entity, 
			HttpServletRequest request) throws Exception {
		// 输出信息
		logger.info("id: {}", id);
		logger.info("product: {}", entity.toString());
		
		
		// 处理图片
		Set<Image> images = entity.getImages();
		if (images != null && images.size() > 0)
			for (Image image : images) {
				// 上传至七牛云服务器
				String path = UploadUtil.uploadImage(image.getPath(), commonConfig);
				if (path == null)
					logger.error("【修改商品】 图片上传失败，base64={}" + image.getPath());
				image.setPath(path);
			}
		entity.setId(id);
		productService.saveOrUpdate(entity);
		return ReturnModel.SUCCESS();
	}
	
	/**
	 * 下架商品
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/{id}", method=RequestMethod.DELETE)
	public ReturnModel cancelProduct(@PathVariable("id") Long id) throws Exception {
		productService.cancel(id);
		return ReturnModel.SUCCESS();
	}
	
	
	/**
	 * 点赞
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/{id}/likes", method=RequestMethod.POST)
	public ReturnModel addLikes(@PathVariable("id") Long id) throws Exception {
		productService.addLikes(id);
		return ReturnModel.SUCCESS();
	}
	
	/**
	 * 取消点赞
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/{id}/likes", method=RequestMethod.DELETE)
	public ReturnModel removeLikes(@PathVariable("id") Long id) throws Exception {
		productService.removeLikes(id);
		return ReturnModel.SUCCESS();
	}
	
	/**
	 * 收藏商品
	 * @throws Exception 
	 */
	@RequestMapping(value="/products/{id}/collection", method=RequestMethod.POST)
	public ReturnModel addCollections(@PathVariable("id") Long id) throws Exception {
		productService.addCollection(id);
		return ReturnModel.SUCCESS();
	}
	
	/**
	 * 取消收藏
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/{id}/collection", method=RequestMethod.DELETE)
	public ReturnModel removeCollection(@PathVariable("id") Long id) throws Exception {
		productService.removeCollection(id);
		return ReturnModel.SUCCESS();
	}
	
	/**
	 * 获取我发布的商品
	 * @return
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/mine", method=RequestMethod.GET)
	public ReturnModel getMine() throws Exception {
		List<Product> productList = productService.getMine();
		List<MineProductVo> resultList = new ArrayList<>();
		
		if (!CollectionUtils.isEmpty(productList)) {
			productList.forEach(e -> {
				MineProductVo mineProductVo = new MineProductVo();
				mineProductVo.setName(e.getName());
				mineProductVo.setPrice(e.getPrice());
				mineProductVo.setProductId(e.getId());
				mineProductVo.setViews(e.getViews());
				mineProductVo.setTime(e.getTmCreated());
				if (!CollectionUtils.isEmpty(e.getImages())) {
					mineProductVo.setPic(e.getImages().iterator().next().getPath());
				}
				
				resultList.add(mineProductVo);
			});
		}
		
		return ReturnModel.SUCCESS(resultList);
	}
	
	/**
	 * 获取我收藏的商品
	 * @return
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/collection", method=RequestMethod.GET)
	public ReturnModel getCollection() throws Exception {
		List<Product> productList = productService.getCollection();
		List<CollProductVo> resultList = new ArrayList<>();
		
		if (!CollectionUtils.isEmpty(productList)) {
			productList.forEach(e -> {
				CollProductVo collProductVo = new CollProductVo();
				collProductVo.setName(e.getName());
				collProductVo.setPrice(e.getPrice());
				collProductVo.setProductId(e.getId());
				collProductVo.setSellerId(e.getMrktUser().getUid());
				if (!CollectionUtils.isEmpty(e.getImages())) {
					collProductVo.setPic(e.getImages().iterator().next().getPath());
				}
				
				resultList.add(collProductVo);
			});
		}
		return ReturnModel.SUCCESS(resultList);
	}
	
	/**
	 * 查看商品的预定留言
	 * @return
	 * @throws Exception 
	 */
	@Authorization
	@RequestMapping(value="/products/{id}/premessage", method=RequestMethod.GET)
	public ReturnModel gePerMessage(@PathVariable("id") Long productId) throws Exception {
		
		return ReturnModel.SUCCESS(productService.getPreMessage(productId));
	}
	
}
