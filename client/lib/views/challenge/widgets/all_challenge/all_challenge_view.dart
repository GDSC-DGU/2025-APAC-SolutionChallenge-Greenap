import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/widgets/common/challenge_category_item.dart';
import 'package:greenap/models/dummy/challenge_dummy.dart';

final backgroundColors = [
  ColorSystem.pinkGradient,
  ColorSystem.blueGradient,
  ColorSystem.yellowGradient,
  ColorSystem.greenGradient,
];

class AllChallengeView extends StatelessWidget {
  const AllChallengeView({super.key});
  @override
  Widget build(BuildContext context) {
    return GridView.count(
      crossAxisCount: 2,
      mainAxisSpacing: 12,
      crossAxisSpacing: 12,
      children: [
        ChallengeCategoryItem(
          category: dummyChallengeCategory[0],
          size: CategorySize.large,
          backgroundGradient: backgroundColors[0],
        ),
        ChallengeCategoryItem(
          category: dummyChallengeCategory[1],
          size: CategorySize.large,
          backgroundGradient: backgroundColors[1],
        ),

        ChallengeCategoryItem(
          category: dummyChallengeCategory[2],
          size: CategorySize.large,
          backgroundGradient: backgroundColors[2],
        ),
        ChallengeCategoryItem(
          category: dummyChallengeCategory[3],
          size: CategorySize.large,
          backgroundGradient: backgroundColors[3],
        ),
      ],
    );
  }
}
