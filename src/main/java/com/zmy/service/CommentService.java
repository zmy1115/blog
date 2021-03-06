package com.zmy.service;

import com.zmy.po.Comment;

import java.util.List;


public interface CommentService {

    List<Comment> listCommentByBlogId(Long blogId);

    Comment saveComment(Comment comment);

    String selectCommentMailById(Long id);

    String selectNameById(Long id);

    List<String> selectEmailList();
}
