import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/widgets/app_bar/default_app_bar.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';
import 'widgets/all_challenge/all_challenge_view.dart';
import 'widgets/my_challenge/my_challenge_view.dart';
import 'package:greenap/widgets/common/custom_toggle_button.dart';
import 'package:get/get.dart';

class ChallengeScreen extends GetView<ChallengeViewModel> {
  ChallengeScreen({super.key}) {
    Get.put(ChallengeViewModel(), permanent: false);
  }
  @override
  Widget build(BuildContext context) {
    final controller = Get.find<ChallengeViewModel>();

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Scaffold(
        backgroundColor: ColorSystem.white,
        appBar: DefaultAppBar(title: '챌린지'),
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 24),
            CustomToggleButton(
              leftText: "둘러보기",
              rightText: "나의 챌린지",
              onToggle: (isLeftSelected) {
                controller.toggleView(isLeftSelected);
              },
            ),
            const SizedBox(height: 24),
            Expanded(
              child: AnimatedSwitcher(
                duration: const Duration(milliseconds: 300),
                child:
                    controller.isLeftSelected.value
                        ? AllChallengeView()
                        : MyChallengeView(),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
