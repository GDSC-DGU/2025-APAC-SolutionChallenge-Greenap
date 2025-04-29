import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/widgets/app_bar/default_app_bar.dart';
import 'package:greenap/views_model/challenge_view_model.dart';
import './widgets/all_challenge/challenge_category_grid.dart';
import 'package:greenap/widgets/common/custom_toggle_button.dart';
import 'package:get/get.dart';

class ChallengeScreen extends GetView<ChallengeViewModel> {
  const ChallengeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Scaffold(
        backgroundColor: ColorSystem.white,
        appBar: DefaultAppBar(title: '챌린지'),
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 24),
            CustomToggleButton(leftText: "둘러보기", rightText: "나의 챌린지"),
            const SizedBox(height: 24),
            Expanded(child: ChallengeCategoryGrid()),
          ],
        ),
      ),
    );
  }
}
