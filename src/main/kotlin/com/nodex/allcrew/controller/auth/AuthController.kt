package com.nodex.allcrew.controller.auth

import com.nodex.allcrew.dto.auth.request.LoginRequest
import com.nodex.allcrew.dto.auth.request.OAuthLoginRequest
import com.nodex.allcrew.dto.auth.request.SignupEmployeeRequest
import com.nodex.allcrew.dto.auth.request.SignupRepresentativeRequest
import com.nodex.allcrew.dto.auth.response.AuthResponse
import com.nodex.allcrew.dto.auth.response.BusinessNumberCheckResponse
import com.nodex.allcrew.dto.auth.response.CompanySlugCheckResponse
import com.nodex.allcrew.dto.auth.response.EmailCheckResponse
import com.nodex.allcrew.dto.auth.response.SignupResponse
import com.nodex.allcrew.service.auth.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse =
        authService.login(request)

    @PostMapping("/oauth/{provider}")
    fun oauthLogin(
        @PathVariable provider: String,
        @Valid @RequestBody request: OAuthLoginRequest,
    ): AuthResponse = authService.oauthLogin(provider, request)

    @PostMapping("/signup/representative")
    fun signupRepresentative(@Valid @RequestBody request: SignupRepresentativeRequest): SignupResponse =
        authService.signupRepresentative(request)

    @PostMapping("/signup/employee")
    fun signupEmployee(@Valid @RequestBody request: SignupEmployeeRequest): SignupResponse =
        authService.signupEmployee(request)

    @GetMapping("/check-email")
    fun checkEmail(@RequestParam email: String): EmailCheckResponse =
        authService.checkEmail(email.trim())

    @GetMapping("/check-business-number")
    fun checkBusinessNumber(@RequestParam businessNumber: String): BusinessNumberCheckResponse =
        authService.checkBusinessNumber(businessNumber.trim())

    @GetMapping("/check-company-slug")
    fun checkCompanySlug(@RequestParam companySlug: String): CompanySlugCheckResponse =
        authService.checkCompanySlug(companySlug.trim())
}
