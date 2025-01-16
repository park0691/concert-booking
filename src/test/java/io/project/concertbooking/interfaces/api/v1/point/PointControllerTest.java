package io.project.concertbooking.interfaces.api.v1.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.domain.point.Point;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.point.repository.PointJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import io.project.concertbooking.interfaces.api.v1.point.request.ChargePointRequest;
import io.project.concertbooking.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[PointController - 통합 테스트]")
@AutoConfigureMockMvc
class PointControllerTest extends IntegrationTestSupport {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    PointJpaRepository pointJpaRepository;

    @Nested
    @DisplayName("[chargeAmount() - 포인트 충전 API 테스트]")
    class ChargeAmountTest {

        @DisplayName("존재하지 않는 유저 아이디로 포인트를 충전하면 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidUserId() throws Exception {
            // given
            ChargePointRequest request = ChargePointRequest.builder()
                    .userId(1L)
                    .point(10000)
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/points/charge")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("US001"));
        }

        @DisplayName("포인트를 충전한다.")
        @Test
        void chargeAmount() throws Exception {
            // given
            User user = userJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(User.class)
                            .sample()
            );
            ChargePointRequest request = ChargePointRequest.builder()
                    .userId(user.getUserId())
                    .point(10000)
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/points/charge")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.currentAmount").isNotEmpty())
                    .andExpect(jsonPath("$.data.currentAmount").value(10000));
        }
    }

    @Nested
    @DisplayName("[getAmount() - 포인트 조회 API 테스트]")
    class GetAmountTest {

        @DisplayName("존재하지 않는 유저 아이디로 포인트를 조회하면 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidUserId() throws Exception {
            // when, then
            mockMvc.perform(
                            get("/api/v1/points/user/1")
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("US001"));
        }

        @DisplayName("포인트를 조회한다.")
        @Test
        void getAmount() throws Exception {
            // given
            User user = userJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(User.class)
                            .sample()
            );
            pointJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Point.class)
                            .set("user", Values.just(user))
                            .set("amount", 123000)
                            .sample()
            );

            // when, then
            mockMvc.perform(
                            get("/api/v1/points/user/%d".formatted(user.getUserId()))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.currentAmount").isNotEmpty())
                    .andExpect(jsonPath("$.data.currentAmount").value(123000));
        }
    }
}