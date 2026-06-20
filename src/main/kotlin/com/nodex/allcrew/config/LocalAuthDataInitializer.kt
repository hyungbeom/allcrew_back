package com.nodex.allcrew.config

import com.nodex.allcrew.domain.AdminMember
import com.nodex.allcrew.mapper.AdminMemberMapper
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@Profile("local")
@Order(1)
class LocalAuthDataInitializer(
    private val adminMemberMapper: AdminMemberMapper,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (adminMemberMapper.existsByEmail("admin@allcrew.com")) {
            return
        }

        adminMemberMapper.insert(
            AdminMember(
                agencyId = 1,
                signupType = "REPRESENTATIVE",
                name = "김대표",
                phone = "01012345678",
                email = "admin@allcrew.com",
                password = passwordEncoder.encode("password123")!!,
                agreeTerms = true,
                agreePrivacy = true,
                agreeLocation = true,
                agreeMarketing = false,
                memberRole = "REPRESENTATIVE",
            ),
        )
    }
}
