import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/widgets/common/challenge_category_item.dart';

final categories = [
  {
    'name': '자원절약',
    'image': 'assets/images/resource/resource.png',
    'color': ColorSystem.pinkGradient,
  },
  {
    'name': '친환경',
    'image': 'assets/images/eco/eco.png',
    'color': ColorSystem.blueGradient,
  },
  {
    'name': '교통절감',
    'image': 'assets/images/transport/transport.png',
    'color': ColorSystem.yellowGradient,
  },
  {
    'name': '재사용',
    'image': 'assets/images/reuse/reuse.png',
    'color': ColorSystem.greenGradient,
  },
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
          category: categories[0],
          size: CategorySize.large,
        ),
        ChallengeCategoryItem(
          category: categories[1],
          size: CategorySize.large,
        ),

        ChallengeCategoryItem(
          category: categories[2],
          size: CategorySize.large,
        ),
        ChallengeCategoryItem(
          category: categories[3],
          size: CategorySize.large,
        ),
      ],
    );
  }
}
