import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/widgets/common/challenge_category_item.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';
import 'package:get/get.dart';

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
    final viewModel = Get.find<ChallengeViewModel>();

    return Obx(() {
      final categories = viewModel.challengeList;

      if (viewModel.isLoading.value) {
        return const Center(child: CircularProgressIndicator());
      }

      if (categories.isEmpty) {
        return const Center(child: Text("챌린지 카테고리를 불러올 수 없습니다."));
      }

      return GridView.builder(
        itemCount: categories.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          mainAxisSpacing: 12,
          crossAxisSpacing: 12,
        ),
        itemBuilder: (context, index) {
          final category = categories[index];
          final background = backgroundColors[index % backgroundColors.length];
          return ChallengeCategoryItem(
            category: category,
            size: CategorySize.large,
            backgroundGradient: background,
          );
        },
      );
    });
  }
}
