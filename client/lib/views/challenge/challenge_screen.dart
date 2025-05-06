import 'package:flutter/material.dart';
import 'package:greenap/widgets/app_bar/default_app_bar.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';
import 'widgets/all_challenge/all_challenge_view.dart';
import 'widgets/my_challenge/my_challenge_view.dart';
import 'package:greenap/widgets/common/custom_toggle_button.dart';
import 'package:get/get.dart';
import 'package:greenap/views/base/base_screen.dart';

class ChallengeScreen extends BaseScreen<ChallengeViewModel> {
  ChallengeScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return DefaultAppBar(title: '챌린지');
  }

  @override
  Widget buildBody(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(height: 24),
          Obx(
            () => CustomToggleButton(
              leftText: "둘러보기",
              rightText: "나의 챌린지",
              isLeftSelected: viewModel.isLeftSelected.value,
              onToggle: (isLeft) => viewModel.toggleView(isLeft),
            ),
          ),
          const SizedBox(height: 24),
          Expanded(
            child: Obx(
              () => AnimatedSwitcher(
                duration: const Duration(milliseconds: 300),
                child:
                    viewModel.isLeftSelected.value
                        ? AllChallengeView(key: ValueKey('all'))
                        : MyChallengeView(key: ValueKey('my')),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
