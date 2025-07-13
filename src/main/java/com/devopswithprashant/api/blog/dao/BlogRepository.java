package com.devopswithprashant.api.blog.dao;

import org.springframework.data.repository.CrudRepository;
import com.devopswithprashant.api.blog.entities.Blog;

public interface BlogRepository extends CrudRepository<Blog, Integer> {
    
    public Blog findById(int id);
    
}
