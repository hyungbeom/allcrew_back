package com.nodex.allcrew.service.auth

import com.nodex.allcrew.domain.AdminAgency
import com.nodex.allcrew.domain.AdminMember
import com.nodex.allcrew.dto.auth.request.LoginRequest
import com.nodex.allcrew.dto.auth.request.SignupEmployeeRequest
import com.nodex.allcrew.dto.auth.request.SignupRepresentativeRequest
import com.nodex.allcrew.dto.auth.response.AuthMemberResponse
import com.nodex.allcrew.dto.auth.response.AuthResponse
import com.nodex.allcrew.dto.auth.response.BusinessNumberCheckResponse
import com.nodex.allcrew.dto.auth.response.CompanySlugCheckResponse
import com.nodex.allcrew.dto.auth.response.EmailCheckResponse
import com.nodex.allcrew.dto.auth.response.SignupResponse
import com.nodex.allcrew.exception.BusinessException
import com.nodex.allcrew.mapper.AdminAgencyMapper
import com.nodex.allcrew.mapper.AdminInviteCodeMapper
import com.nodex.allcrew.mapper.AdminMemberMapper
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val adminMemberMapper: AdminMemberMapper,
    private val adminAgencyMapper: AdminAgencyMapper,
    private val adminInviteCodeMapper: AdminInviteCodeMapper,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    fun checkEmail(email: String): EmailCheckResponse {
        val exists = adminMemberMapper.existsByEmail(email)
        return EmailCheckResponse(available = !exists)
    }

    fun checkBusinessNumber(businessNumber: String): BusinessNumberCheckResponse {
        val exists = adminAgencyMapper.findByBusinessNumber(businessNumber) != null
        return BusinessNumberCheckResponse(available = !exists)
    }

    fun checkCompanySlug(companySlug: String): CompanySlugCheckResponse {
        val normalized = normalizeCompanySlug(companySlug)
        validateCompanySlugFormat(normalized)
        val exists = adminAgencyMapper.existsBySlug(normalized)
        return CompanySlugCheckResponse(available = !exists)
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val member = adminMemberMapper.findByEmail(request.email.trim())
            ?: throw BusinessException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.")

        if (!member.isActive) {
            throw BusinessException(HttpStatus.UNAUTHORIZED, "비활성화된 계정입니다.")
        }

        if (!passwordEncoder.matches(request.password, member.password)) {
            throw BusinessException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.")
        }

        adminMemberMapper.updateLastLoginAt(member.id!!)
        return buildAuthResponse(member)
    }

    @Transactional
    fun signupRepresentative(request: SignupRepresentativeRequest): SignupResponse {
        validateRequiredAgreements(request.agreeTerms, request.agreePrivacy, request.agreeLocation)

        if (adminMemberMapper.existsByEmail(request.email.trim())) {
            throw BusinessException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.")
        }

        if (adminAgencyMapper.findByBusinessNumber(request.businessNumber) != null) {
            throw BusinessException(HttpStatus.CONFLICT, "이미 등록된 사업자등록번호입니다.")
        }

        val companySlug = normalizeCompanySlug(request.companySlug)
        validateCompanySlugFormat(companySlug)

        if (adminAgencyMapper.existsBySlug(companySlug)) {
            throw BusinessException(HttpStatus.CONFLICT, "이미 사용 중인 기업 영문 이름입니다.")
        }

        val agency = AdminAgency(
            companyName = request.companyName.trim(),
            companySlug = companySlug,
            businessNumber = request.businessNumber.trim(),
            address = request.address?.trim(),
            addressDetail = request.addressDetail?.trim(),
        )
        adminAgencyMapper.insert(agency)

        val member = AdminMember(
            agencyId = agency.id,
            signupType = "REPRESENTATIVE",
            name = request.name.trim(),
            phone = request.phone.trim(),
            email = request.email.trim(),
            password = passwordEncoder.encode(request.password)!!,
            agreeTerms = request.agreeTerms,
            agreePrivacy = request.agreePrivacy,
            agreeLocation = request.agreeLocation,
            agreeMarketing = request.agreeMarketing,
            memberRole = "REPRESENTATIVE",
        )
        adminMemberMapper.insert(member)

        return SignupResponse(message = "회원가입이 완료되었습니다.", memberId = member.id!!)
    }

    @Transactional
    fun signupEmployee(request: SignupEmployeeRequest): SignupResponse {
        validateRequiredAgreements(request.agreeTerms, request.agreePrivacy, request.agreeLocation)

        if (adminMemberMapper.existsByEmail(request.email.trim())) {
            throw BusinessException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.")
        }

        val inviteCode = adminInviteCodeMapper.findByCode(request.inviteCode.trim())
            ?: throw BusinessException(HttpStatus.BAD_REQUEST, "유효하지 않은 초대 코드입니다.")

        if (inviteCode.usedAt != null) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "이미 사용된 초대 코드입니다.")
        }

        if (inviteCode.expiresAt != null && inviteCode.expiresAt.isBefore(LocalDateTime.now())) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "만료된 초대 코드입니다.")
        }

        val member = AdminMember(
            agencyId = inviteCode.agencyId,
            signupType = "EMPLOYEE",
            name = request.name.trim(),
            phone = request.phone.trim(),
            email = request.email.trim(),
            password = passwordEncoder.encode(request.password)!!,
            agreeTerms = request.agreeTerms,
            agreePrivacy = request.agreePrivacy,
            agreeLocation = request.agreeLocation,
            agreeMarketing = request.agreeMarketing,
            inviteCodeUsed = inviteCode.code,
            memberRole = "EMPLOYEE",
        )
        adminMemberMapper.insert(member)
        adminInviteCodeMapper.markUsed(inviteCode.id!!, member.id!!)

        return SignupResponse(message = "회원가입이 완료되었습니다.", memberId = member.id!!)
    }

    private fun validateRequiredAgreements(agreeTerms: Boolean, agreePrivacy: Boolean, agreeLocation: Boolean) {
        if (!agreeTerms || !agreePrivacy || !agreeLocation) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "필수 약관에 모두 동의해 주세요.")
        }
    }

    private fun normalizeCompanySlug(raw: String): String =
        raw.trim().lowercase().replace(Regex("\\s+"), "-")

    private fun validateCompanySlugFormat(companySlug: String) {
        if (!companySlug.matches(Regex("^[a-z0-9]+(?:-[a-z0-9]+)*$"))) {
            throw BusinessException(
                HttpStatus.BAD_REQUEST,
                "기업 영문 이름은 영문 소문자, 숫자, 하이픈(-)만 사용할 수 있습니다.",
            )
        }

        if (companySlug.length !in 2..50) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "기업 영문 이름은 2~50자로 입력해 주세요.")
        }

        if (companySlug in RESERVED_COMPANY_SLUGS) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "사용할 수 없는 기업 영문 이름입니다.")
        }
    }

    private fun buildAuthResponse(member: AdminMember): AuthResponse {
        val agency = member.agencyId?.let { adminAgencyMapper.findById(it) }
        val token = jwtTokenProvider.createToken(member, agency?.companySlug)
        return AuthResponse(
            accessToken = token,
            member = AuthMemberResponse(
                id = member.id!!,
                name = member.name,
                email = member.email,
                phone = member.phone,
                memberRole = member.memberRole,
                signupType = member.signupType,
                agencyId = member.agencyId,
                companyName = agency?.companyName,
                companySlug = agency?.companySlug,
            ),
        )
    }

    companion object {
        private val RESERVED_COMPANY_SLUGS = setOf(
            "login",
            "signup",
            "api",
            "admin",
            "www",
            "dashboard",
            "mypage",
        )
    }
}
