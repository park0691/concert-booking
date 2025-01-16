package io.project.concertbooking.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration
@Profile("(!test-no-auditing & test) | !(test-no-auditing | test)")
public class JpaAuditingConfig {
}
