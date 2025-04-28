package com.app.server.user_challenge.application.service

import com.app.server.user_challenge.domain.enums.EUserChallengeCertificationStatus
import com.app.server.user_challenge.domain.model.UserChallenge
import com.app.server.user_challenge.domain.model.UserChallengeHistory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class UserChallengeHistoryCommandService (
) {

    fun createUserChallengeHistory(userChallenge: UserChallenge, participantsDate: Int) {
        val userChallengeHistoryList = mutableListOf<UserChallengeHistory>()

        for (i in 0..participantsDate.minus(1)) {
            val history = createUserChallengeHistory(i, userChallenge)
            userChallengeHistoryList.add(history)
        }
        userChallenge.addUserChallengeHistories(userChallengeHistoryList)

    }

    private fun createUserChallengeHistory(i: Int, userChallenge: UserChallenge): UserChallengeHistory {
        val challengeDate = LocalDate.now().plusDays(i.toLong())
        return UserChallengeHistory(
            id = null,
            userChallenge = userChallenge,
            date = challengeDate,
            status = EUserChallengeCertificationStatus.FAILED,
            certificatedImageUrl = null
        )
    }

}
