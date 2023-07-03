package ee.kemit.aks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class AksBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AksBeApplication.class, args);
	}

}
