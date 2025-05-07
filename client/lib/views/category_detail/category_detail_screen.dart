import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/category_detail/category_detail_view_model.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/widgets/app_bar/back_app_bar.dart';
import 'package:greenap/models/challenge_category.dart';
import './widgets/challenge_card.dart';

class CategoryDetailScreen extends BaseScreen<CategoryDetailViewModel> {
  CategoryDetailScreen({super.key});

  final ChallengeCategoryModel category = Get.arguments;

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return BackAppBar(title: viewModel.category.title);
  }

  @override
  Widget buildBody(BuildContext context) {
    final challenges = viewModel.category.challenges;

    return Padding(
      padding: const EdgeInsets.all(20),
      child: ListView.separated(
        itemCount: challenges.length,
        separatorBuilder: (_, __) => const SizedBox(height: 20),
        itemBuilder: (context, index) {
          final challenge = challenges[index];
          return ChallengeCard(challenge: challenge);
        },
      ),
    );
  }
}
