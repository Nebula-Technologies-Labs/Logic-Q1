package com.logicq1.crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest(
		properties = {
				"spring.datasource.url=jdbc:h2:mem:testdb",
				"spring.datasource.driver-class-name=org.h2.Driver",
				"spring.datasource.username=sa",
				"spring.datasource.password=",
				"spring.jpa.hibernate.ddl-auto=create-drop",

				"spring.security.oauth2.client.registration.google.client-id=dummy",
				"spring.security.oauth2.client.registration.google.client-secret=dummy",

				"jwt.secret=testsecretkey123456789",

				"spring.mail.host=localhost",
				"spring.mail.port=1025",
				"spring.mail.username=test@example.com",
				"spring.mail.password=test"
		}
)
class CrmApplicationTests {

	@MockitoBean
	private JavaMailSender javaMailSender;

	@Test
	void contextLoads() {
	}

}