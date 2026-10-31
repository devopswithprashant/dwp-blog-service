package com.devopswithprashant.service.blog.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.devopswithprashant.service.blog.SecurityConfig;
import com.devopswithprashant.service.blog.application.BlogService;
import com.devopswithprashant.service.blog.infrastructure.metrics.BlogMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BlogController.class, properties = {
        "JWT_SECRET=012345678901234567890123456789012345678901234567"
})
@ImportAutoConfiguration({SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class})
@Import(SecurityConfig.class)
class BlogSecurityTests {

    private static final UUID AUTHOR_UUID = UUID.fromString("96c8bbf1-22b1-49c6-8a0d-e31f9f9df31d");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlogService blogService;

    @MockitoBean
    private BlogMetrics blogMetrics;

    @Test
    void publicBlogReadsDoNotRequireAuthentication() throws Exception {
        when(blogService.getAllBlogs(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/blogs"))
                .andExpect(status().isOk());
    }

    @Test
    void creatingBlogRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Post\",\"markdown\":\"Body\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void creationUsesVerifiedSubjectAndIgnoresBodyAuthorId() throws Exception {
        when(blogService.createDraft(AUTHOR_UUID, "Post", "Body")).thenReturn(7L);

        mockMvc.perform(post("/api/blogs")
                        .with(jwt().jwt(token -> token.subject(AUTHOR_UUID.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Post\",\"markdown\":\"Body\",\"authorId\":\"" +
                                UUID.randomUUID() + "\"}"))
                .andExpect(status().isOk());

        verify(blogService).createDraft(AUTHOR_UUID, "Post", "Body");
    }

    @Test
    void crossUserManagementReturnsForbidden() throws Exception {
        doThrow(new AccessDeniedException("You are not allowed to manage this blog"))
                .when(blogService).deleteBlog(7L, AUTHOR_UUID);

        mockMvc.perform(delete("/api/blogs/7")
                        .with(jwt().jwt(token -> token.subject(AUTHOR_UUID.toString()))))
                .andExpect(status().isForbidden());
    }
}
