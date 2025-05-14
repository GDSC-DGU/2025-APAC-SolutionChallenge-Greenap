import 'package:flutter/material.dart';
import 'package:greenap/domain/enums/ranking.dart';
import 'package:greenap/views_model/ranking/ranking_view_model.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:get/get.dart';
import 'user_ranking_card.dart';

class RankingContentView extends StatelessWidget {
  final bool isLeftSelected;
  const RankingContentView({super.key, required this.isLeftSelected});

  @override
  Widget build(BuildContext context) {
    final viewModel = Get.find<RankingViewModel>();

    return Obx(() {
      if (viewModel.isLoading.value) {
        return const Center(child: CircularProgressIndicator());
      }

      if (isLeftSelected) {
        // 전체 랭킹
        final ranking = viewModel.allRanking.value;
        final userRank = viewModel.myRanking.value;

        if (ranking == null) {
          return const Text('아직 랭킹이 없습니다.');
        }

        final totalParticipants = ranking.totalParticipantsCount;
        final top100 = ranking.top100Participants;

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (userRank != null)
              UserRankingCard(rankingUser: userRank, rankType: RankType.all),
            Text(
              '총 ${_formatNumber(totalParticipants)}명 참여중',
              style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
            ),
            const SizedBox(height: 4),
            Text(
              'TOP 100',
              style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
            ),
            const SizedBox(height: 16),

            ...top100.map((rankedUser) {
              // final medalAsset =
              //     rankedUser.rank <= 3
              //         ? 'assets/icons/medal_${rankedUser.rank}.svg'
              //         : null;

              return UserRankingCard(
                rankingUser: rankedUser,
                rankType: RankType.all,
              );
            }),
          ],
        );
      } else {
        // 누적 랭킹
        final ranking = viewModel.challengeRanking.value;
        final userRank = viewModel.myChallengeRanking.value;

        if (ranking == null) {
          return const Text('아직 랭킹이 없습니다.');
        }

        final top100 = ranking.top100Participants;

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (userRank != null)
              UserRankingCard(
                rankingUser: userRank,
                rankType: RankType.challenge,
              ),
            Obx(() {
              final challenges = viewModel.challengeList;

              return Container(
                width: double.infinity, // 화면 너비만큼 확장
                padding: const EdgeInsets.symmetric(
                  horizontal: 12,
                ), // optional padding
                decoration: BoxDecoration(
                  color: ColorSystem.white,
                  borderRadius: BorderRadius.circular(4),
                  border: Border.all(color: ColorSystem.gray[200]!),
                ),
                child: DropdownButtonHideUnderline(
                  child: DropdownButton<int>(
                    isExpanded: true,

                    hint: Text(
                      '챌린지를 선택하세요',
                      style: FontSystem.Button3.copyWith(
                        color: ColorSystem.gray[700],
                      ),
                    ),
                    value: viewModel.selectedChallengeId.value,
                    onChanged: (int? id) {
                      if (id != null) {
                        viewModel.selectedChallengeId.value = id;
                        viewModel.fetchCumulativeRanking(id);
                      }
                    },
                    items:
                        challenges.map((challenge) {
                          return DropdownMenuItem<int>(
                            value: challenge.id,
                            child: Text(challenge.title),
                          );
                        }).toList(),
                  ),
                ),
              );
            }),
            const SizedBox(height: 24),
            Text(
              '총 ${_formatNumber(ranking.totalParticipantsCount)}명 참여중',
              style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
            ),
            const SizedBox(height: 4),
            Text(
              'TOP 100',
              style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
            ),
            const SizedBox(height: 24),

            ...top100.map((rankedUser) {
              // final medalAsset =
              //     rankedUser.rank <= 3
              //         ? 'assets/icons/medal_${rankedUser.rank}.svg'
              //         : null;

              return UserRankingCard(
                rankingUser: rankedUser,
                rankType: RankType.challenge,
              );
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
