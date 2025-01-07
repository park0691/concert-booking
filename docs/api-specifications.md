## API Specifications
### 1. 대기열 토큰 발급
#### Description
- 콘서트 좌석 예약을 위한 대기열 토큰을 발급한다.

#### Endpoint
- Method : `POST`
- URL : `/api/v1/queue/tokens`

#### REQUEST
- Body
| 항목 | 설명 |
| -- | -- |
| userId | 사용자 ID |

- Body Example
```json
{
    "userId": 2
}
```

#### RESPONSE Example (200)
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
#### Description
- 콘서트의 예약 가능한 스케줄 정보를 조회한다.

#### Endpoint
- Method : `GET`
- URL : `/api/v1/concerts/{concertId}/schedules`

#### REQUEST
- Header
| 항목 | 설명 |
| -- | -- |
| Queue-Token | 대기열 토큰 값 |

- Path Variables
| 항목 | 설명 |
| -- | -- |
| concertId | 콘서트 ID |

#### RESPONSE Example (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "concertId": 2,
        "schedules": [
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
}
```

### 3. 콘서트 좌석 조회
#### Description
- 예약 가능한 콘서트의 좌석을 조회한다.

#### Endpoint
- Method : `GET`
- URL : `/api/v1/concerts/schedules/{concertScheduleId}/seats`

#### REQUEST
- Header
| 항목 | 설명 |
| -- | -- |
| Queue-Token | 대기열 토큰 값 |

- Path Variables
| 항목 | 설명 |
| -- | -- |
| concertScheduleId | 콘서트 스케줄 ID |

#### RESPONSE Example (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "concertScheduleId" : 11,
        "seats": [
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
}
```

### 4. 콘서트 좌석 예약
#### Description
- 콘서트 좌석의 예약을 요청한다.

#### Endpoint
- Method : `POST`
- URL : `/api/v1/concerts/schedules/{concertScheduleId}/reservation/seats/{seatId}`

#### REQUEST
- Header
| 항목 | 설명 |
| -- | -- |
| Queue-Token | 대기열 토큰 값 |

- Path Variables
| 항목 | 설명 |
| -- | -- |
| concertScheduleId | 콘서트 스케줄 ID |
| seatId | 좌석 ID |

- Body
| 항목 | 설명 |
| -- | -- |
| userId | 사용자 ID |

- Body Example
```json
{
    "userId": 2
}
```

#### RESPONSE (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "seatReservationId": 1,
        "concertScheduleId": 2,
        "seatNumber": 11,
        "price": 10000
    }
}
```

### 5. 포인트 잔액 조회
#### Description
- 포인트 잔액을 조회한다.

#### Endpoint
- Method : `GET`
- URL : `/api/v1/points`


#### REQUEST
- Body
| 항목 | 설명 |
| -- | -- |
| userId | 사용자 ID |

- Body Example
```json
{
    "userId": 2
}
```

#### RESPONSE Example (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "pointAmount": 10000
    }
}
```

### 6. 포인트 잔액 충전
#### Description
- 포인트 잔액을 충전한다.

#### Endpoint
- Method : `POST`
- URL : `/api/v1/points/charge`


#### REQUEST
- Body
| 항목 | 설명 |
| -- | -- |
| userId | 사용자 ID |
| point | 포인트 충전 금액 |

- Body Example
```json
{
    "userId": 2,
    "point": 30000
}
```

#### RESPONSE Example (200)
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "currentPointAmount": 10000
    }
}
```

### 7. 예약  좌석 결제
#### Description
- 예약한 좌석을 결제 처리, 결제 내역을 생성한다.

#### Endpoint
- Method : `POST`
- URL : `/api/v1/payments/seat-reservations/{seatReservationId}`

#### REQUEST
- Header
| 항목 | 설명 |
| -- | -- |
| Queue-Token | 대기열 토큰 값 |

- Path Variables
| 항목 | 설명 |
| -- | -- |
| seatReservationId | 좌석 예약 ID |

- Body
| 항목 | 설명 |
| -- | -- |
| userId | 사용자 ID |
| paymentPrice | 결제 금액 |

- Body Example
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