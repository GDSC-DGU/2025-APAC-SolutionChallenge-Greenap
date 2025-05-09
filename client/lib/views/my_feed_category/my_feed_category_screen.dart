import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:get/get.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/widgets/app_bar/back_app_bar.dart';
import 'package:greenap/widgets/common/challenge_category_item.dart';
import 'package:greenap/domain/models/dummy/challenge_dummy.dart';

final backgroundColors = [
  ColorSystem.pinkGradient,
  ColorSystem.blueGradient,
  ColorSystem.yellowGradient,
  ColorSystem.greenGradient,
];

class MyFeedCategoryScreen extends BaseScreen {
  const MyFeedCategoryScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return BackAppBar(title: "내 피드");
  }

  @override
  Widget buildBody(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: GridView.count(
        crossAxisCount: 2,
        mainAxisSpacing: 12,
        crossAxisSpacing: 12,
        children: [
          ChallengeCategoryItem(
            category: dummyChallengeCategory[0],
            size: CategorySize.large,
            backgroundGradient: backgroundColors[0],
            onTap: () {
              Get.toNamed(
                '/my-feed-list',
                arguments: dummyChallengeCategory[0].id,
              );
            },
          ),
          ChallengeCategoryItem(
            category: dummyChallengeCategory[1],
            size: CategorySize.large,
            backgroundGradient: backgroundColors[1],
            onTap: () {
              Get.toNamed(
                '/my-feed-list',
                arguments: dummyChallengeCategory[1].id,
              );
            },
          ),

          ChallengeCategoryItem(
            category: dummyChallengeCategory[2],
            size: CategorySize.large,
            backgroundGradient: backgroundColors[2],
            onTap: () {
              Get.toNamed(
                '/my-feed-list',
                arguments: dummyChallengeCategory[2].id,
              );
            },
          ),
          ChallengeCategoryItem(
            category: dummyChallengeCategory[3],
            size: CategorySize.large,
            backgroundGradient: backgroundColors[3],
            onTap: () {
              Get.toNamed(
                '/my-feed-list',
                arguments: dummyChallengeCategory[3].id,
              );
            },
          ),
        ],
      ),
    );
  }
}
