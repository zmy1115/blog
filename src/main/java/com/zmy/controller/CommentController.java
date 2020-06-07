package com.zmy.controller;


import com.zmy.po.Comment;
import com.zmy.po.User;
import com.zmy.service.BlogService;
import com.zmy.service.CommentService;
import com.zmy.util.CodeUtil;
import com.zmy.util.SensitiveFilter;
import com.zmy.util.emailUtil.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
public class CommentController {

    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;
    @Value("${comment.avatar}")
    private String avatar;
    @Autowired
    private Producer producer;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        return "blog :: commentList";
    }


    @PostMapping("/comments")
    public String post(Comment comment, HttpSession session, HttpServletRequest request,
                       RedirectAttributes attributes) {
            Long blogId = comment.getBlog().getId();
            comment.setBlog(blogService.getBlog(blogId));
            User user = (User) session.getAttribute("user");
            if (!CodeUtil.checkVerifyCode(comment.getYzm(),request)) {
            attributes.addFlashAttribute("message", "验证码打错了");
           return "redirect:/comments/" + blogId;
            }
            if (user != null) {
                comment.setAvatar(user.getAvatar());
                comment.setAdminComment(true);
            } else {
                comment.setAvatar(avatar);
            }
            commentService.saveComment(comment);
            return "redirect:/comments/" + blogId;
        }

    }



