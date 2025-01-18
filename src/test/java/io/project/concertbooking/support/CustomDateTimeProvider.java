package io.project.concertbooking.support;

import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

@Profile("test-no-auditing")
@Component
public class CustomDateTimeProvider implements DateTimeProvider {

    private LocalDateTime userDefinedTime;

    public void setUserDefinedTime(LocalDateTime userDefinedTime) {
        this.userDefinedTime = userDefinedTime;
    }

    @Override
    public Optional<TemporalAccessor> getNow() {
        Optional<TemporalAccessor> dateTimeOpt = Optional.ofNullable(userDefinedTime);
        return dateTimeOpt.or(() -> Optional.of(LocalDateTime.now()));
    }
}
