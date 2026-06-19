package com.nodex.allcrew.exception

import org.springframework.http.HttpStatus

/**
 * 비즈니스 규칙 위반 시 발생하는 예외.
 *
 * Service 계층에서 의도적으로 던지며, [GlobalExceptionHandler]가
 * HTTP 상태 코드와 메시지로 변환해 클라이언트에 반환한다.
 *
 * @param status 클라이언트에 반환할 HTTP 상태 코드 (예: 404, 409)
 * @param message 클라이언트에 표시할 오류 메시지
 */
class BusinessException(
    val status: HttpStatus,
    override val message: String,
) : RuntimeException(message)
