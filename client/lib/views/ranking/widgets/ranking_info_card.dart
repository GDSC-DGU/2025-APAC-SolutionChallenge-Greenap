import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

class RankingInfoCard extends StatelessWidget {
  final bool isLeftSelected;
  const RankingInfoCard({super.key, required this.isLeftSelected});

  @override
  Widget build(BuildContext context) {
    final title = isLeftSelected ? "전체 랭킹" : "누적 챌린지 랭킹";
    final description =
        isLeftSelected
            ? "전체 랭킹은 전체 챌린지 대상 연속 일수에 대한 랭킹입니다.매일매일 챌린지를 잊지 말고 랭킹 상승에 도전하세요!"
            : "누적 챌린지 랭킹 챌린지별 전체 참여 일수에 대한 랭킹입니다. 연속 참여 일수와 관계 없이 가장 많이 챌린지에 참여한 순위를 확인합니다!";

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
      decoration: BoxDecoration(
        color: ColorSystem.mint.withOpacity(0.05),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: FontSystem.Head3.copyWith(color: ColorSystem.gray[800]),
          ),
          const SizedBox(height: 4),
          Text(
            description,
            style: FontSystem.Body3.copyWith(color: ColorSystem.gray[600]),
          ),
        ],
      ),
    );
  }
}
