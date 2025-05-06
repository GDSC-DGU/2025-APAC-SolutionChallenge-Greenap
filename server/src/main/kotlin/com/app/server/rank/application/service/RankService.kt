package com.app.server.rank.application.service

import com.app.server.common.exception.BadRequestException
import com.app.server.rank.exception.RankException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RankService (
    private val redisTemplate: RedisTemplate<String, Any>
){

    fun updateValue(key: String, value: Long, score: Long): Boolean? {
        redisTemplate.opsForZSet().remove(key, value)
        return redisTemplate.opsForZSet().add(key, value, score.toDouble())
    }

    fun getScore(key: String, value: Long): Double {
        return redisTemplate.opsForZSet().score(key, value)
            ?: 0.0
    }

    fun getRank(key: String, value: Long): Long {
        return redisTemplate.opsForZSet().reverseRank(key, value)
        ?: throw BadRequestException(RankException.NOT_FOUND_RANK)
    }

    fun getTop100UserInfo(key: String): List<Pair<Any, Double>> {
        val userIdAndScores = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 99)
            ?: return emptyList()

        return userIdAndScores.map { userIdAndScore ->
            val userId = userIdAndScore.value
                ?: throw BadRequestException(RankException.NOT_FOUND_USER_RANK)
            val score = userIdAndScore.score ?: 0.0
            userId to score
        }
    }

    fun count(key: String): Long? {
        return redisTemplate.opsForZSet().size(key)
            ?:0
    }

}