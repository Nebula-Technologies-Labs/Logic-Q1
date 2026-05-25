package com.logicq1.crm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		properties = {
				"spring.datasource.url=jdbc:h2:mem:testdb",
				"spring.datasource.driver-class-name=org.h2.Driver",
				"spring.datasource.username=sa",
				"spring.datasource.password=",
				"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
				"spring.jpa.hibernate.ddl-auto=create-drop",

				"spring.security.oauth2.client.registration.google.client-id=dummy",
				"spring.security.oauth2.client.registration.google.client-secret=dummy",

				"jwt.secret=testsecretkey123456789"
		}
)
class CrmApplicationTests {

	@Test
	void contextLoads() {
	}

}