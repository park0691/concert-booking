package io.project.concertbooking.domain.payment;

import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.domain.concert.Concert;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.payment.enums.PaymentMethod;
import io.project.concertbooking.domain.reservation.Reservation;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.concert.repository.ConcertJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.SeatJpaRepository;
import io.project.concertbooking.infrastructure.payment.repository.PaymentJpaRepository;
import io.project.concertbooking.infrastructure.reservation.ReservationJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import io.project.concertbooking.support.IntegrationTestSupport;
import net.jqwik.api.Arbitraries;
import net.jqwik.time.api.DateTimes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[PaymentService - 통합 테스트]")
class PaymentServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    PaymentService paymentService;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Autowired
    PaymentJpaRepository paymentJpaRepository;

    @Nested
    @DisplayName("createPayment() - 테스트")
    class CreatePayment {

        @Transactional
        @DisplayName("결제를 생성한다.")
        @Test
        void createPayment() {
            // given
            User user = userJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(User.class)
                            .sample()
            );
            Concert concert = concertJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Concert.class)
                            .set("name", Arbitraries.strings().withCharRange('A', 'Z').ofLength(10).map(s -> s + " Concert"))
                            .sample()
            );
            LocalDateTime now = LocalDateTime.now();
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

            // when
            Payment payment = paymentService.createPayment(user, reservation, seat.getPrice(), PaymentMethod.POINT);

            // then
            Optional<Payment> paymentOpt = paymentJpaRepository.findById(payment.getPaymentId());
            assertThat(paymentOpt.isPresent()).isTrue();

            Payment foundPayment = paymentOpt.get();
            assertThat(foundPayment.getPaymentId()).isEqualTo(payment.getPaymentId());
        }
    }
}