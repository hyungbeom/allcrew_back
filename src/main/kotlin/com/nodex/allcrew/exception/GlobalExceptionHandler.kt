package com.nodex.allcrew.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리기.
 *
 * Controller에서 발생한 예외를 일관된 JSON 형식([ErrorResponse])으로 변환한다.
 * 클라이언트는 항상 `{ status, message }` 구조의 오류 응답을 받는다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리 (404, 409 등).
     *
     * Service에서 의도적으로 던진 [BusinessException]의 status와 message를 그대로 반환한다.
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(exception.status)
            .body(ErrorResponse(exception.status.value(), exception.message))

    /**
     * 요청 바디 유효성 검사 실패 처리.
     *
     * `@Valid` 검증 실패 시 필드별 오류 메시지를 하나의 문자열로 합쳐 400 반환한다.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = exception.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(HttpStatus.BAD_REQUEST.value(), message))
    }

    /**
     * 예상하지 못한 서버 오류 처리.
     *
     * 내부 오류 상세는 로그로 남기고, 클라이언트에는 일반 메시지만 반환한다.
     */
    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다."))
}

/**
 * API 오류 응답 공통 형식.
 *
 * @param status HTTP 상태 코드 숫자 (예: 400, 404)
 * @param message 사람이 읽을 수 있는 오류 설명
 */
data class ErrorResponse(
    val status: Int,
    val message: String,
)
