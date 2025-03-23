package com.devopswithprashant.api.blog.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devopswithprashant.api.blog.dao.BlogRepository;
import com.devopswithprashant.api.blog.entities.Blog;

@Component
public class BlogService {

    //private static List<Blog> list = new ArrayList<>();

    // static {
    //     list.add(new Blog(2875, "git roadmap", "this is my content of git", "Verma, Shreya", "11-08-2023", "31-01-2024"));
    //     list.add(new Blog(2345, "DevOps roadmap", "This is my test body content", "Prajapati, Prashant", "31-10-2022", "10-06-2023"));
    //     list.add(new Blog(56535, "linux roadmap", "this is my content of linux", "Prajapati, Prashant", "11-05-2021", "01-01-2024"));
    //     list.add(new Blog(98875, "networking roadmap", "this is my content of networking", "Verma, Shreya", "11-08-2020", "31-01-2021"));
    // }

    @Autowired
    private BlogRepository blogRepository;

    // get all blog
    public List<Blog> getAllBlogs() {
        List<Blog> list = (List<Blog>)this.blogRepository.findAll();
        return list;
    }

    // get specific blog by ID 
    public Blog getBlogById(int id) {
        Blog blog = null;
        try {
            //blog=list.stream().filter(e->e.getId()==id).findFirst().get();
            blog = this.blogRepository.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blog;
    }

    //adding the blog
    public Blog addBlog(Blog blog) {
        //list.add(blog);
        return this.blogRepository.save(blog);
    }

    //delete the blog
    public void deleteblog(int id) {
        try {
            //list=list.stream().filter(blog -> blog.getId() != id).collect(Collectors.toList());
            this.blogRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateblog(Blog blog, int id) {
        // list = list.stream().map(b-> {
        //     if(b.getId() == id) {
        //         b.setTitle(blog.getTitle());
        //         b.setBody(blog.getBody());
        //         b.setAuthor(blog.getAuthor());
        //         b.setLastupdatedate(blog.getLastupdatedate());
        //         b.setPublishdate(blog.getPublishdate());
        //     }
        //     return b;
        // }).collect(Collectors.toList());
        blog.setId(id);
        this.blogRepository.save(blog);
    }

}
