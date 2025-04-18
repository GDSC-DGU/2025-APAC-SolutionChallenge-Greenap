package com.app.server.common.security.info

import com.app.server.common.constant.Constants
import com.app.server.user.model.User
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.RequiredArgsConstructor
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails (
    private val id: Long,
    private val email: String,
    private val authorities: Collection<GrantedAuthority>

): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun getId(): Long {
        return id
    }

    companion object {
        @JvmStatic
        fun create(user: User): CustomUserDetails {
            val authority = SimpleGrantedAuthority(Constants.ROLE_PREFIX + Constants.USER_ROLE_CLAIM_NAME)
            val authorities: MutableCollection<GrantedAuthority> = ArrayList()
            authorities.add(authority)
            return CustomUserDetails(
                id = user.id,
                email = user.email,
                authorities = authorities
            )
        }
    }
}
