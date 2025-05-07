import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/widgets/common/challenge_category_item.dart';
import 'package:greenap/domain/models/dummy/challenge_dummy.dart';

class ChallengeCategoryGrid extends StatelessWidget {
  ChallengeCategoryGrid({super.key});

  final backgroundColors = [
    ColorSystem.pinkGradient,
    ColorSystem.blueGradient,
    ColorSystem.yellowGradient,
    ColorSystem.greenGradient,
  ];

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 200,
      child: Row(
        children: [
          // 왼쪽 큰 카테고리
          Expanded(
            flex: 3,
            child: ChallengeCategoryItem(
              category: dummyChallengeCategory[0],
              size: CategorySize.large,
              backgroundGradient: backgroundColors[0],
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            flex: 4,
            child: Column(
              children: [
                // 중간 카테고리 (위 절반)
                Expanded(
                  flex: 2,
                  child: SizedBox.expand(
                    child: ChallengeCategoryItem(
                      category: dummyChallengeCategory[1],
                      size: CategorySize.medium,
                      backgroundGradient: backgroundColors[1],
                    ),
                  ),
                ),
                const SizedBox(height: 12),
                // 작은 카테고리 2개 (아래 절반)
                Expanded(
                  flex: 2,
                  child: Row(
                    children: [
                      Expanded(
                        child: ChallengeCategoryItem(
                          category: dummyChallengeCategory[2],
                          size: CategorySize.small,
                          backgroundGradient: backgroundColors[2],
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: ChallengeCategoryItem(
                          category: dummyChallengeCategory[3],
                          size: CategorySize.small,
                          backgroundGradient: backgroundColors[3],
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
