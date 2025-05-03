package com.app.server.core.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken(authorities: Collection<GrantedAuthority?>?, private val userId: Long) :
    AbstractAuthenticationToken(authorities) {
    override fun getCredentials(): Any {
        return this.userId
    }

    override fun getPrincipal(): Any {
        return this.userId
    }
}
