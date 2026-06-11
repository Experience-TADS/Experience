package com.senai.experience;

import com.senai.experience.config.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ExperienceApplicationTests extends PostgresTestContainer {

	@Test
	void contextLoads() {
	}

}
