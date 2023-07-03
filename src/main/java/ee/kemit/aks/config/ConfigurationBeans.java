package ee.kemit.aks.config;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class ConfigurationBeans {

	private static final int TIMEOUT_MS = 5_000;

	@Bean
	public WebClient genericWebClient() {
		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(genericConnectionClient())).build();
	}

	public HttpClient genericConnectionClient() {
		return HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_MS)
				.responseTimeout(Duration.ofMillis(TIMEOUT_MS));
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**").allowedOrigins("http://localhost:4000");
			}
		};
	}

	@Bean(name = "auditingDateTimeProvider")
	public DateTimeProvider dateTimeProvider() {
		return () -> Optional.of(OffsetDateTime.now());
	}
}