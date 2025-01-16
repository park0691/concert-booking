package io.project.concertbooking.interfaces.api.v1.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.constants.QueueConstants;
import io.project.concertbooking.common.util.TokenGenerateUtil;
import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.queue.repository.QueueJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import io.project.concertbooking.interfaces.api.v1.queue.request.IssueQueueTokenRequest;
import io.project.concertbooking.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[ConcertController - 통합 테스트]")
@AutoConfigureMockMvc
class QueueControllerTest extends IntegrationTestSupport {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    QueueConstants queueConstants;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    QueueJpaRepository queueJpaRepository;

    @Nested
    @DisplayName("[issueQueueToken() - 대기열 토큰 발급 API 테스트]")
    class IssueQueueToken {

        @DisplayName("존재하지 않는 유저 아이디로 대기열 토큰 발급 요청시 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidUserId() throws Exception {
            // given
            IssueQueueTokenRequest request = IssueQueueTokenRequest.builder()
                    .userId(1L)
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/queue/tokens")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("US001"));
        }

        @DisplayName("대기열 토큰을 발급한다.")
        @Test
        void issueQueueToken() throws Exception {
            // given
            User user = userJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(User.class)
                            .sample()
            );

            IssueQueueTokenRequest request = IssueQueueTokenRequest.builder()
                    .userId(user.getUserId())
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/queue/tokens")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").isNotEmpty())
                    .andExpect(jsonPath("$.data.expireDt").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("[getQueueStatus() - 대기열 토큰 상태 조회 API 테스트]")
    class GetQueueStatus {

        @DisplayName("헤더에 대기열 토큰이 없으면 에러 응답을 반환한다.")
        @Test
        void requestWithEmptyHeaderToken() throws Exception {
            mockMvc.perform(
                            get("/api/v1/queue")
                    )
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("발급되지 않은 토큰으로 토큰 상태 조회 시 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidToken() throws Exception {
            // given
            String token = TokenGenerateUtil.generateUUIDToken();

            // when, then
            mockMvc.perform(
                            get("/api/v1/queue")
                                    .header(queueConstants.getRequestHeaderKey(), token)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("QE001"));
        }

        @DisplayName("대기열 토큰 상태 조회에 성공한다.")
        @Test
        void getQueueStatus() throws Exception {
            // given
            User user = userJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(User.class)
                            .sample()
            );
            String token = TokenGenerateUtil.generateUUIDToken();
            LocalDateTime now = LocalDateTime.now();
            Queue queue = queueJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Queue.class)
                            .set("user", Values.just(user))
                            .set("status", Values.just(QueueStatus.ACTIVATED))
                            .set("token", Values.just(token))
                            .set("expDt", now.plusMinutes(10L))
                            .sample()
            );

            // when, then
            mockMvc.perform(
                            get("/api/v1/queue")
                                    .header(queueConstants.getRequestHeaderKey(), token)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").isNotEmpty())
                    .andExpect(jsonPath("$.data.expireDt").isNotEmpty());
        }
    }
}