package io.project.concertbooking.support;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(dateTimeProviderRef = "customDateTimeProvider")
@Configuration
@Profile("test-no-auditing")
public class JpaAuditingConfig {
}
