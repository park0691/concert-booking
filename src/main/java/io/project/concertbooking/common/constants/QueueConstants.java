package io.project.concertbooking.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "queue")
@AllArgsConstructor
public class QueueConstants {

    private final String requestHeaderKey;
    private final int maxSize;
    private final int expireTimeMin;
}
