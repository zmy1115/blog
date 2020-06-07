package com.zmy.controller.adminController;

import com.zmy.po.Blog;
import com.zmy.po.User;
import com.zmy.service.BlogService;
import com.zmy.service.CommentService;
import com.zmy.service.TagService;
import com.zmy.service.TypeService;
import com.zmy.util.emailUtil.Producer;
import com.zmy.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";


    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private Producer producer;

    //管理界面呈现
    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return LIST;
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog, Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return "admin/blogs :: blogList";
    }


    @GetMapping("/blogs/input")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }


    //管理界面查找
    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog", blog);
        return INPUT;
    }


    //增加博客
    @PostMapping("/blogs")
    public String post(HttpServletRequest response, Blog blog, RedirectAttributes attributes, HttpSession session, @RequestParam(value = "file") MultipartFile file) {
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
        Blog b;

        if (file.isEmpty()){
            blog.setTupian(null);
        }
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File("/home/jar/img/" + file.getOriginalFilename())));//保存图片到目录下
                out.write(file.getBytes());
                out.flush();
                out.close();

                String filename = "http://lisiyuanblog.com/img/" + file.getOriginalFilename();
                blog.setTupian(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                attributes.addFlashAttribute("message", "操作失败");

            } catch (IOException e) {
                e.printStackTrace();
                attributes.addFlashAttribute("message", "操作失败");
            }
        }
        if (blog.getId() == null) {
            b = blogService.saveBlog(blog);
        } else {
            b = blogService.updateBlog(blog.getId(), blog);
        }


        if (b == null) {
            attributes.addFlashAttribute("message", "操作失败");
        } else {
            List<String> emailList = commentService.selectEmailList();
            for (int i=0;i<emailList.size();i++){
                producer.send("Ailun发布了新博客，有空去看看吧","Hi，Ailun发布了最新博客："+blog.getTitle()+"。快去看看吧:"+blog.getId(),emailList.get(i));
            }
            attributes.addFlashAttribute("message", "操作成功");
        }
        return REDIRECT_LIST;
    }


    //删除博客
    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "删除成功");
        return REDIRECT_LIST;
    }


}
