package com.devopswithprashant.api.blog;

//import java.util.Optional;
import java.util.Arrays;
import com.devopswithprashant.api.blog.entities.Blog;
import com.devopswithprashant.api.blog.service.BlogService;
import com.devopswithprashant.api.blog.dao.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @InjectMocks
    private BlogService blogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBlogs() {
        // Arrange
        Blog blog1 = new Blog(20875, "git roadmap", "this is my content of git", "Verma, Shubham", "11-08-2023", "31-01-2024");
		Blog blog2 = new Blog(50535, "linux roadmap", "this is my content of linux", "Prajapati, Mohit", "11-05-2021", "01-01-2024");
        
        when(blogRepository.findAll()).thenReturn(Arrays.asList(blog1, blog2));

        // Act
        var blogs = blogService.getAllBlogs();

        // Assert
        assertEquals(2, blogs.size());
        assertEquals("git roadmap", blogs.get(0).getTitle());
        verify(blogRepository, times(1)).findAll();
    }

    @Test
    void testGetBlogById_Success() {
        // Arrange
        Blog blog = new Blog(2875, "git roadmap", "this is my content of git", "Verma, Shubham", "11-08-2023", "31-01-2024");

        when(blogRepository.findById(2875)).thenReturn(blog);

        // Act
		Blog foundBlog = blogService.getBlogById(2875);

        // Assert
        assertNotNull(foundBlog);
        assertEquals(2875, foundBlog.getId());
        assertEquals("Verma, Shubham", foundBlog.getAuthor());
    }

    @Test
    void testGetBlogById_NotFound() {
        // Arrange
        //Blog blog = new Blog(2875, "git roadmap", "this is my content of git", "Verma, Shubham", "11-08-2023", "31-01-2024");

        int blogId = 100;
        when(blogRepository.findById(blogId)).thenReturn(null);
        
        // Act
        Blog foundBlog = blogService.getBlogById(blogId);

        // Assert        
        assertNull(foundBlog);
    }

    @Test
    void testCreateBlog() {
        // Arrange
        Blog blog = new Blog(2875, "git roadmap", "this is my content of git", "Verma, Shubham", "11-08-2023", "31-01-2024");

        when(blogRepository.save(blog)).thenReturn(blog);

        // Act
        Blog savedBlog = blogService.addBlog(blog);

        // Assert
        assertNotNull(savedBlog);
        assertEquals("git roadmap", savedBlog.getTitle());
        verify(blogRepository, times(1)).save(blog);
    }

}
