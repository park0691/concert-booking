package io.project.concertbooking.interfaces.api.v1.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.constants.QueueConstants;
import io.project.concertbooking.common.util.TokenGenerateUtil;
import io.project.concertbooking.domain.concert.Concert;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.point.Point;
import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.reservation.Reservation;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.concert.repository.ConcertJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.SeatJpaRepository;
import io.project.concertbooking.infrastructure.point.repository.PointJpaRepository;
import io.project.concertbooking.infrastructure.queue.repository.QueueJpaRepository;
import io.project.concertbooking.infrastructure.reservation.ReservationJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import io.project.concertbooking.interfaces.api.v1.payment.request.PaymentRequest;
import io.project.concertbooking.support.IntegrationTestSupport;
import net.jqwik.api.Arbitraries;
import net.jqwik.time.api.DateTimes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[PaymentController - 통합 테스트]")
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest extends IntegrationTestSupport {

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

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Autowired
    PointJpaRepository pointJpaRepository;

    @Nested
    @DisplayName("[payments() - 결제 API 테스트]")
    class Payments {

        @DisplayName("발급되지 않은 토큰으로 요청시 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidToken() throws Exception {
            mockMvc.perform(
                            post("/api/v1/payments/reservations/1")
                                    .header(queueConstants.getRequestHeaderKey(), "bla_bla_bla")
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("QE001"));
        }

        @DisplayName("활성화되지 않은 토큰으로 요청시 에러 응답을 반환한다.")
        @ParameterizedTest
        @EnumSource(value = QueueStatus.class, names = {"WAITING", "EXPIRED"})
        void requestWithNonActivatedToken(QueueStatus queueStatus) throws Exception {
            // given
            LocalDateTime now = LocalDateTime.now();
            String token = TokenGenerateUtil.generateUUIDToken();
            queueJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Queue.class)
                            .setNull("user")
                            .set("status", Values.just(queueStatus))
                            .set("token", Values.just(token))
                            .set("expDt", now.plusMinutes(10L))
                            .sample()
            );

            // when, then
            mockMvc.perform(
                            post("/api/v1/payments/reservations/1")
                                    .header(queueConstants.getRequestHeaderKey(), token)
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("QE002"));
        }

        @DisplayName("존재하지 않는 유저 아이디로 결제 요청시 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidUserId() throws Exception {
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

            PaymentRequest request = PaymentRequest.builder()
                    .userId(user.getUserId() + 1)
                    .paymentPrice(10000)
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/payments/reservations/1")
                                    .header(queueConstants.getRequestHeaderKey(), token)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("US001"));
        }

        @DisplayName("존재하지 않는 예약 아이디로 결제 요청시 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidReservationId() throws Exception {
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

            PaymentRequest request = PaymentRequest.builder()
                    .userId(user.getUserId())
                    .paymentPrice(10000)
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/payments/reservations/1")
                                    .header(queueConstants.getRequestHeaderKey(), token)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("RE001"));
        }

        @DisplayName("포인트가 부족한 경우 에러 응답을 반환한다.")
        @Test
        void requestWithInSufficientPoint() throws Exception {
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
            Concert concert = concertJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Concert.class)
                            .set("name", Arbitraries.strings().withCharRange('A', 'Z').ofLength(10).map(s -> s + " Concert"))
                            .sample()
            );
            ConcertSchedule schedule = concertScheduleJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                            .set("concert", Values.just(concert))
                            .set("scheduleDt", DateTimes.dateTimes().atTheEarliest(now.plusDays(7L))
                                    .atTheLatest(now.plusDays(60L)))
                            .sample()
            );
            Seat seat = seatJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Seat.class)
                            .set("concertSchedule", Values.just(schedule))
                            .set("number", 1)
                            .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                            .set("status", Values.just(SeatStatus.RESERVED))
                            .sample()
            );
            Reservation reservation = reservationJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Reservation.class)
                            .set("user", Values.just(user))
                            .set("seat", Values.just(seat))
                            .set("seatNumber", seat.getNumber())
                            .set("status", ReservationStatus.RESERVED)
                            .set("regDt", now.minusMinutes(7))
                            .sample()
            );

            PaymentRequest request = PaymentRequest.builder()
                    .userId(user.getUserId())
                    .paymentPrice(seat.getPrice())
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/payments/reservations/%d".formatted(reservation.getReservationId()))
                                    .header(queueConstants.getRequestHeaderKey(), token)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("PT002"));

        }

        @DisplayName("예약된 좌석을 결제한다.")
        @Test
        void payments() throws Exception {
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
            Concert concert = concertJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Concert.class)
                            .set("name", Arbitraries.strings().withCharRange('A', 'Z').ofLength(10).map(s -> s + " Concert"))
                            .sample()
            );
            ConcertSchedule schedule = concertScheduleJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                            .set("concert", Values.just(concert))
                            .set("scheduleDt", DateTimes.dateTimes().atTheEarliest(now.plusDays(7L))
                                    .atTheLatest(now.plusDays(60L)))
                            .sample()
            );
            Seat seat = seatJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Seat.class)
                            .set("concertSchedule", Values.just(schedule))
                            .set("number", 1)
                            .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                            .set("status", Values.just(SeatStatus.RESERVED))
                            .sample()
            );
            Reservation reservation = reservationJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Reservation.class)
                            .set("user", Values.just(user))
                            .set("seat", Values.just(seat))
                            .set("seatNumber", seat.getNumber())
                            .set("status", ReservationStatus.RESERVED)
                            .set("regDt", now.minusMinutes(7))
                            .sample()
            );
            Point point = pointJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Point.class)
                            .set("user", Values.just(user))
                            .set("amount", seat.getPrice() + 10000)
                            .sample()
            );

            PaymentRequest request = PaymentRequest.builder()
                    .userId(user.getUserId())
                    .paymentPrice(seat.getPrice())
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/payments/reservations/%d".formatted(reservation.getReservationId()))
                                    .header(queueConstants.getRequestHeaderKey(), token)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.paymentId").isNumber())
                    .andExpect(jsonPath("$.data.reservationId").value(reservation.getReservationId()))
                    .andExpect(jsonPath("$.data.price").value(seat.getPrice()))
                    .andExpect(jsonPath("$.data.paymentDt").exists());

        }
    }
}