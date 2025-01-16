package io.project.concertbooking.interfaces.api.v1.concert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.constants.QueueConstants;
import io.project.concertbooking.common.util.TokenGenerateUtil;
import io.project.concertbooking.domain.concert.Concert;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.concert.repository.ConcertJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.SeatJpaRepository;
import io.project.concertbooking.infrastructure.queue.repository.QueueJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import io.project.concertbooking.interfaces.api.v1.concert.request.ReservationRequest;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[ConcertController - 통합 테스트]")
@AutoConfigureMockMvc
class ConcertControllerIntegrationTest extends IntegrationTestSupport {

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

    @Nested
    @DisplayName("[reservations() - 콘서트 좌석 예약 API 통합 테스트]")
    class ReservationsTest {

        @DisplayName("발급되지 않은 토큰으로 요청시 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidToken() throws Exception {
            mockMvc.perform(
                            post("/api/v1/concerts/schedules/1/reservations")
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
                            post("/api/v1/concerts/schedules/1/reservations")
                                    .header(queueConstants.getRequestHeaderKey(), token)
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("QE002"));
        }

        @DisplayName("존재하지 않는 유저 아이디로 예약 요청시 에러 응답을 반환한다.")
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

            ReservationRequest request = ReservationRequest.builder()
                    .userId(-1L)
                    .seatIds(List.of(1L, 2L))
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/concerts/schedules/1/reservations")
                                    .header(queueConstants.getRequestHeaderKey(), token)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("US001"));
        }

        @DisplayName("존재하지 않는 콘서트 스케줄로 예약 요청시 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidScheduleId() throws Exception {
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

            ReservationRequest request = ReservationRequest.builder()
                    .userId(user.getUserId())
                    .seatIds(List.of(1L, 2L))
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/concerts/schedules/1/reservations")
                                    .header(queueConstants.getRequestHeaderKey(), token)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("CN001"));
        }

        @DisplayName("존재하지 않는 좌석으로 예약 요청시 에러 응답을 반환한다.")
        @Test
        void requestWithInvalidSeatId() throws Exception {
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

            ReservationRequest request = ReservationRequest.builder()
                    .userId(user.getUserId())
                    .seatIds(List.of(1L, 2L))
                    .build();

            // when, then
            mockMvc.perform(
                    post("/api/v1/concerts/schedules/%s/reservations".formatted(schedule.getConcertScheduleId()))
                            .header(queueConstants.getRequestHeaderKey(), token)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
            )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("CN003"));
        }

        @DisplayName("예약 요청한 좌석이 이미 예약된 자석인 경우 에러 응답을 반환한다.")
        @ParameterizedTest
        @EnumSource(value = SeatStatus.class, names = {"RESERVED", "OCCUPIED"})
        void requestWithAlreadyReservedSeat(SeatStatus status) throws Exception {
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
            AtomicInteger seatNumber = new AtomicInteger(0);
            List<Seat> seats = fixtureMonkey.giveMeBuilder(Seat.class)
                    .set("concertSchedule", Values.just(schedule))
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.EMPTY))
                    .sampleList(10);
            seatJpaRepository.saveAll(seats);
            Seat notEmptySeat = seatJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Seat.class)
                            .set("concertSchedule", Values.just(schedule))
                            .setLazy("number", () -> seatNumber.addAndGet(1))
                            .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                            .set("status", Values.just(status))
                            .sample()
            );

            Seat firstSeat = seats.get(2);
            ReservationRequest request = ReservationRequest.builder()
                    .userId(user.getUserId())
                    .seatIds(List.of(firstSeat.getSeatId(), notEmptySeat.getSeatId()))
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/concerts/schedules/%s/reservations".formatted(schedule.getConcertScheduleId()))
                                    .header(queueConstants.getRequestHeaderKey(), token)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("CN004"));
        }

        @DisplayName("활성화된 토큰으로 콘서트를 예약한다.")
        @Test
        void reservations() throws Exception {
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
            AtomicInteger seatNumber = new AtomicInteger(0);
            List<Seat> seats = fixtureMonkey.giveMeBuilder(Seat.class)
                    .set("concertSchedule", Values.just(schedule))
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.EMPTY))
                    .sampleList(10);
            seatJpaRepository.saveAll(seats);



            Seat firstSeat = seats.get(2);
            Seat secondSeat = seats.get(8);
            ReservationRequest request = ReservationRequest.builder()
                    .userId(user.getUserId())
                    .seatIds(List.of(firstSeat.getSeatId(), secondSeat.getSeatId()))
                    .build();

            // when, then
            mockMvc.perform(
                            post("/api/v1/concerts/schedules/%s/reservations".formatted(schedule.getConcertScheduleId()))
                                    .header(queueConstants.getRequestHeaderKey(), token)
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.reservations").isArray())
                    .andExpect(jsonPath("$.data.reservations[0].concertScheduleId").value(schedule.getConcertScheduleId()))
                    .andExpect(jsonPath("$.data.reservations[0].seatNumber").value(firstSeat.getNumber()))
                    .andExpect(jsonPath("$.data.reservations[0].price").value(firstSeat.getPrice()))
                    .andExpect(jsonPath("$.data.reservations[1].concertScheduleId").value(schedule.getConcertScheduleId()))
                    .andExpect(jsonPath("$.data.reservations[1].seatNumber").value(secondSeat.getNumber()))
                    .andExpect(jsonPath("$.data.reservations[1].price").value(secondSeat.getPrice()));
        }
    }
}