## API Specifications
### 1. 대기열 토큰 발급
- 콘서트 좌석 예약을 위한 대기열 토큰을 발급한다.

#### REQUEST
- POST `/api/v1/concerts/{concertScheduleId}/queue-token`

#### RESPONSE (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "token": "123e4567-e89b-12d3-a456-426614174000"
    }
}
```

### 2. 콘서트 예약 가능 날짜 조회
- 콘서트의 예약 가능한 스케줄 정보를 조회한다.

#### REQUEST
- GET `/api/v1/concerts/{concertId}/reservation/schedules`
    - HEADER `Queue-Token: 123e4567-e89b-12d3-a456-426614174000`

#### RESPONSE (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": [
        {
            "scheduleId": 11,
            "scheduleDt": "2025-05-01T15:00:00"
        },
        {
            "scheduleId": 12,
            "scheduleDt": "2025-05-08T15:00:00"
        }
    ]
}
```

### 3. 콘서트 좌석 조회
- 예약 가능한 콘서트의 좌석을 조회한다.

#### REQUEST
- GET `/api/v1/concerts/{concertId}/reservation/seats`
    - HEADER `Queue-Token: 123e4567-e89b-12d3-a456-426614174000`

#### RESPONSE (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": [
        {
            "seatId": 1,
            "seatNumber": 10,
            "price": 10000
        },
        {
            "seatId": 2,
            "seatNumber": 12,
            "price": 20000
        }
    ]
}
```

### 4. 콘서트 좌석 예약
- 콘서트 좌석의 예약을 요청한다.

#### REQUEST
- POST `/api/v1/concerts/{consertScheduleId}/reservation/seats/{seatId}`
    - HEADER `Queue-Token: 123e4567-e89b-12d3-a456-426614174000`

```json
{
    "userId": "testuser",
    "price": 30000
}
```

#### RESPONSE (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "seatReservationId": 1,
        "seatNumber": 11,
        "price": 10000
    }
}
```

### 5. 캐시 조회
- 캐시 잔액을 조회한다.

#### REQUEST
- GET `/api/v1/users/cash`

```json
{
    "userId": "testuser"
}
```

#### RESPONSE (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "cash": 10000
    }
}
```

### 6. 캐시 충전
- 캐시를 충전한다.

#### REQUEST
- POST `/api/v1/users/cash/charge`

```json
{
    "userId": "testuser",
    "cash": 30000
}
```

#### RESPONSE (200)
```json
{
    "code": 200,
    "message": "Success"
}
```

### 7. 예약  좌석 결제
- 예약한 좌석을 결제한다.

#### REQUEST
- POST `/api/v1/concerts/{consertScheduleId}/reservation/seats/payments/{seatId}`
    - HEADER `Queue-Token: 123e4567-e89b-12d3-a456-426614174000`

```json
{
    "userId": "testuser",
    "price": 30000
}
```

#### RESPONSE (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "paymentId": 12,
        "seatReservationId": 11,
        "concertScheduleId": 1,
        "concertScheduledDt": "2025-05-08T15:00:00"
    }
}
```