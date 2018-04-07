package com.mrkt.product.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrkt.constant.ResultEnum;
import com.mrkt.exception.MrktException;
import com.mrkt.product.dao.CommentRepository;
import com.mrkt.product.model.Comment;

/**
 * @ClassName	CommentServiceImpl
 * @Description 商品留言的实现
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/02/23 16:29:54
 */
@Service(value="commentService")
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	private final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
	
	@Override
	@Transactional
	public void removeComment(Long id) throws Exception {
		if (id == null) {
			logger.error("【删除评论】 评论的主键id为空");
			throw new MrktException(ResultEnum.COMMENT_NOT_EXIST);
		}
		commentRepository.delete(id);
	}

	@Override
	public Comment getCommentById(Long id) throws Exception {
		Comment comment = commentRepository.findOne(id);
		if (comment == null) {
			logger.error("【查询评论】 商品留言评论不存在，commentId={}", id);
			throw new MrktException(ResultEnum.COMMENT_NOT_EXIST);
		}
		return comment;
	}

}
