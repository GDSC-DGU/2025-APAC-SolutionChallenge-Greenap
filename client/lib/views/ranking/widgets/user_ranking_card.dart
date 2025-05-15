import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/domain/models/all_ranking.dart';
import 'package:greenap/domain/enums/ranking.dart';
import 'package:flutter_svg/flutter_svg.dart';

class UserRankingCard extends StatelessWidget {
  final ParticipantModel rankingUser;
  final RankType rankType;
  const UserRankingCard({
    super.key,
    required this.rankingUser,
    required this.rankType,
  });

  @override
  Widget build(BuildContext context) {
    final nickname = rankingUser.user.nickname;
    final profileImageUrl = rankingUser.user.profileImageUrl;

    String subtitleText = '';
    switch (rankType) {
      case RankType.all:
        subtitleText =
            '최장 연속 일수 ${rankingUser.user.longestConsecutiveParticipationCount}일';
        break;
      case RankType.challenge:
        subtitleText =
            rankingUser.user.totalParticipationCount != null
                ? '총 ${rankingUser.user.totalParticipationCount}회 참여'
                : '';
        break;
    }
    String? medalAsset;
    if (rankingUser.rank != null && rankingUser.rank! <= 3) {
      medalAsset = 'assets/icons/medal_${rankingUser.rank}.svg';
    }

    return Container(
      padding: const EdgeInsets.all(8),
      margin: const EdgeInsets.only(bottom: 24),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.04),
            blurRadius: 4,
            spreadRadius: 2,
            offset: Offset(0, 0),
          ),
        ],
      ),
      child: Row(
        children: [
          if (medalAsset != null)
            SvgPicture.asset(medalAsset, width: 32, height: 32)
          else
            Padding(
              padding: const EdgeInsets.only(left: 12, right: 8),
              child: Text(
                '${rankingUser.rank ?? '-'}',
                style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
              ),
            ),
          const SizedBox(width: 12),
          Expanded(
            child: ListTile(
              contentPadding: EdgeInsets.zero,
              dense: true,
              minLeadingWidth: 0,
              leading: CircleAvatar(
                radius: 24,
                backgroundImage: NetworkImage(profileImageUrl),
              ),
              title: Text(
                nickname,
                style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
              ),
              subtitle: Text(
                subtitleText,
                style: FontSystem.Body2.copyWith(color: ColorSystem.gray[600]),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
