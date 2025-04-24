package com.app.server.user_challenge.application.repository

import com.app.server.user_challenge.domain.model.UserChallengeHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserChallengeHistoryRepository : JpaRepository<UserChallengeHistory, Long> {

}
