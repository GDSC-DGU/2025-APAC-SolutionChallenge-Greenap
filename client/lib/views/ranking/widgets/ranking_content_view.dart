import 'package:flutter/material.dart';
import 'package:greenap/views_model/ranking/ranking_view_model.dart';
import 'package:get/get.dart';
import 'user_ranking_card.dart';

class RankingContentView extends StatelessWidget {
  final bool isLeftSelected;
  const RankingContentView({super.key, required this.isLeftSelected});

  @override
  Widget build(BuildContext context) {
    final viewModel = Get.find<RankingViewModel>();

    return Obx(() {
      if (isLeftSelected) {
        // 전체 랭킹
        final ranking = viewModel.allRanking.value;
        final userRank = viewModel.myRanking.value;

        if (ranking == null || userRank == null) {
          return const Center(child: CircularProgressIndicator());
        }

        final totalParticipants = ranking.totalParticipantsCount;
        final top100 = ranking.top100Participants;

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            UserRankingCard(rankingUser: userRank),

            Text(
              '총 ${_formatNumber(totalParticipants)}명 참여중',
              style: const TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 4),
            const Text(
              'TOP 100',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),

            ...top100.map((rankedUser) {
              // final medalAsset =
              //     rankedUser.rank <= 3
              //         ? 'assets/icons/medal_${rankedUser.rank}.svg'
              //         : null;

              return UserRankingCard(rankingUser: rankedUser);
            }),
          ],
        );
      } else {
        // 누적 랭킹
        final ranking = viewModel.challengeRanking.value;

        if (ranking == null) {
          return const Center(child: Text("조회된 랭킹이 없습니다."));
        }

        final top100 = ranking.top100Participants;

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            DropdownButton<int>(
              value: viewModel.selectedChallengeId.value,
              hint: Text("챌린지를 선택해주세요"),
              items:
                  viewModel.availableChallenges.map((challenge) {
                    return DropdownMenuItem<int>(
                      value: challenge.id,
                      child: Text(challenge.title),
                    );
                  }).toList(),
              onChanged: (selectedId) {
                if (selectedId != null) {
                  viewModel.selectedChallengeId.value = selectedId;
                  viewModel.fetchCumulativeRanking(selectedId);
                }
              },
            ),
            Text(
              ranking.challengeTitle,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              '총 ${_formatNumber(ranking.totalParticipantsCount)}명 참여중',
              style: const TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 4),
            const Text(
              'TOP 100',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),

            ...top100.map((rankedUser) {
              // final medalAsset =
              //     rankedUser.rank <= 3
              //         ? 'assets/icons/medal_${rankedUser.rank}.svg'
              //         : null;

              return UserRankingCard(rankingUser: rankedUser);
            }),
          ],
        );
      }
    });
  }

  String _formatNumber(int value) {
    return value.toString().replaceAllMapped(
      RegExp(r'\B(?=(\d{3})+(?!\d))'),
      (match) => ',',
    );
  }
}
