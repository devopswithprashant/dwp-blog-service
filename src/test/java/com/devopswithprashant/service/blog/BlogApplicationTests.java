package com.devopswithprashant.service.blog;


//import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"JWT_SECRET=012345678901234567890123456789012345678901234567",
		"PROFILE_SERVICE_BASE_URL=http://profile.test"
})
class BlogApplicationTests {

	@Test
	void contextLoads() {
	}

}
