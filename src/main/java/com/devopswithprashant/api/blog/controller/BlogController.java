package com.devopswithprashant.api.blog.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import com.devopswithprashant.api.blog.entities.Blog;
import com.devopswithprashant.api.blog.service.BlogService;



@RestController
public class BlogController {

    @Autowired
    private BlogService blogService;

    //get all blog handler
    @GetMapping("/blogs")
    public ResponseEntity<List<Blog>> getBlogs() {
        List<Blog> list = this.blogService.getAllBlogs();
        if (list.size() <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.of(Optional.of(list));
    }

    //get single blog handler
    @GetMapping("/blogs/{id}")
    public ResponseEntity<Blog> getBlog(@PathVariable("id") int id) {
        Blog blog = this.blogService.getBlogById(id);
        if(blog == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.of(Optional.of(blog));
    }

    //add new blog handler
    @PostMapping("/blogs")
    public ResponseEntity<Blog> addBlog(@RequestBody Blog blog) {
        try {
            Blog savedblog = this.blogService.addBlog(blog);
            //return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            return ResponseEntity.ok().body(savedblog);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //delete a blog by ID
    @DeleteMapping("/blog/{blogID}")
    public ResponseEntity<Void> deleteBlog(@PathVariable("blogID") int id) {
        try {
            this.blogService.deleteblog(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //update blog handler
    @PutMapping("/blog/{id}")
    public ResponseEntity<Blog> putUpdateBlog(@PathVariable("id") int id, @RequestBody Blog blog) {
        try {
            this.blogService.updateblog(blog, id);
            return ResponseEntity.ok().body(blog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }
    
    
    
    
   

}
