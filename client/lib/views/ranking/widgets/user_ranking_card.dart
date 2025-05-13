import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/domain/models/all_ranking.dart';

class UserRankingCard extends StatelessWidget {
  final ParticipantModel rankingUser;
  const UserRankingCard({super.key, required this.rankingUser});

  @override
  Widget build(BuildContext context) {
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
          // SvgPicture.asset('assets/icons/medal_1.svg', width: 32, height: 32),
          const SizedBox(width: 12),
          Expanded(
            child: ListTile(
              contentPadding: EdgeInsets.zero,
              dense: true,
              minLeadingWidth: 0,
              leading: CircleAvatar(
                radius: 24,
                backgroundImage: NetworkImage(rankingUser.user.profileImageUrl),
              ),
              title: Text(
                rankingUser.user.nickname,
                style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
              ),
              subtitle: Text(
                '최장 연속 일수 ${rankingUser.user.longestConsecutiveParticipationCount}일',
                style: FontSystem.Body2.copyWith(color: ColorSystem.gray[600]),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
