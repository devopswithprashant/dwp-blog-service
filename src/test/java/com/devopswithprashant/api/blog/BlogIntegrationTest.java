package com.devopswithprashant.api.blog;

//package com.example.blog;

import com.devopswithprashant.api.blog.entities.Blog;
import com.devopswithprashant.api.blog.service.BlogService;
import com.devopswithprashant.api.blog.dao.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

//import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class BlogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogService blogService;

    @BeforeEach
    void setUp() {
        blogRepository.deleteAll(); // Clear the database before each test

        // Prepopulate the database with some test data
        Blog blog1 = new Blog();
        blog1.setId(1);
        blog1.setTitle("First Blog");
        blog1.setBody("This is the first blog.");
        blog1.setAuthor("Bob");
        blog1.setPublishdate("31-10-2022");
        blog1.setLastupdatedate("23-09-2023");
        blogRepository.save(blog1);

        Blog blog2 = new Blog();
        blog1.setId(2);
        blog2.setTitle("Second Blog");
        blog2.setBody("This is the second blog.");
        blog2.setAuthor("William, Sean");
        blog2.setPublishdate("03-10-2010");
        blog2.setLastupdatedate("23-09-2013");
        blogRepository.save(blog2);
        
    }

    @Test
    void testGetAllBlogs() throws Exception {
        mockMvc.perform(get("/blogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("First Blog")))
                .andExpect(jsonPath("$[1].title", is("Second Blog")));
    }

    @Test
    void testGetBlogById() throws Exception {
        Blog blog = blogService.getAllBlogs().get(0);

        mockMvc.perform(get("/blogs/{id}", blog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(blog.getTitle())))
                .andExpect(jsonPath("$.body", is(blog.getBody())));
    }

    @Test
    void testCreateBlog() throws Exception {
        String newBlogJson = """
                {
                    "title": "monitoring roadmap",
                    "body": "this is my content of monitoring",
                    "author": "Prajapati, Prashant",
                    "publishdate": "21-11-2024",
                    "lastupdatedate": "16-12-2024"
                }
                """;

        mockMvc.perform(post("/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBlogJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("monitoring roadmap")))
                .andExpect(jsonPath("$.body", is("this is my content of monitoring")));

        // Verify that the new blog is saved in the database
        assertEquals(3, blogService.getAllBlogs().size());
    }

    @Test
    void testDeleteBlog() throws Exception {
        Blog blog = blogService.getAllBlogs().get(0);

        mockMvc.perform(delete("/blog/{id}", blog.getId()))
                .andExpect(status().isNoContent());

        // Verify that the blog is deleted from the database
        assertEquals(1, blogService.getAllBlogs().size());
    }

    @Test
    void testGetBlogById_NotFound() throws Exception {
        mockMvc.perform(get("/blogs/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
