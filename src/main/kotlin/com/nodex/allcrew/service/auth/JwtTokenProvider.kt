package com.nodex.allcrew.service.auth

import com.nodex.allcrew.domain.AdminMember
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${app.jwt.secret:allcrew-dev-secret-key-change-in-production-32chars}")
    secret: String,
    @Value("\${app.jwt.expiration-ms:86400000}")
    private val expirationMs: Long,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8).copyOf(32))

    fun createToken(member: AdminMember, companySlug: String? = null): String {
        val now = System.currentTimeMillis()
        val builder = Jwts.builder()
            .subject(member.id.toString())
            .claim("email", member.email)
            .claim("memberRole", member.memberRole)
            .claim("agencyId", member.agencyId)
            .issuedAt(Date(now))
            .expiration(Date(now + expirationMs))

        if (companySlug != null) {
            builder.claim("companySlug", companySlug)
        }

        return builder.signWith(key).compact()
    }

    fun getMemberId(token: String): Long {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        return claims.subject.toLong()
    }
}
