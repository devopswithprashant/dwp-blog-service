package com.devopswithprashant.service.blog.infrastructure.metrics;

import com.devopswithprashant.service.blog.application.BlogService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "JWT_SECRET=012345678901234567890123456789012345678901234567",
        "PROFILE_SERVICE_BASE_URL=http://profile.test",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BlogMetricsExportTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createDraftExportsCreatedAndVersionCountersToPrometheus() throws Exception {
        String title = "metrics-test-" + UUID.randomUUID();
        blogService.createDraft(UUID.randomUUID(), title, "# body");

        assertThat(registry.find("blog.drafts").counter())
                .isNotNull()
                .satisfies(c -> assertThat(c.count()).isGreaterThanOrEqualTo(1.0));
        assertThat(registry.find("blog.versions.saved").counter())
                .isNotNull()
                .satisfies(c -> assertThat(c.count()).isGreaterThanOrEqualTo(1.0));

        String prometheus = mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(prometheus).contains("blog_drafts_total");
        assertThat(prometheus).contains("blog_versions_saved_total");
    }
}
