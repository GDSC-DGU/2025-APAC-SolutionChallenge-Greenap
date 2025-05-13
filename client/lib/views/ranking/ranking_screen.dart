import 'package:flutter/material.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/widgets/app_bar/back_app_bar.dart';
import 'package:greenap/views_model/ranking/ranking_view_model.dart';
import 'package:greenap/widgets/common/custom_toggle_button.dart';
import 'package:get/get.dart';
import './widgets/ranking_content_view.dart';
import './widgets/ranking_info_card.dart';

class RankingScreen extends BaseScreen<RankingViewModel> {
  RankingScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return BackAppBar(title: "랭킹");
  }

  @override
  Widget buildBody(BuildContext context) {
    return Obx(() {
      return Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 24),
            CustomToggleButton(
              leftText: "전채 랭킹",
              rightText: "누적 챌린지 랭킹",
              isLeftSelected: viewModel.isLeftSelected.value,
              onToggle: (isLeft) => viewModel.toggleView(isLeft),
            ),
            const SizedBox(height: 24),
            RankingInfoCard(isLeftSelected: viewModel.isLeftSelected.value),

            const SizedBox(height: 24),
            Expanded(
              child: AnimatedSwitcher(
                duration: const Duration(milliseconds: 300),
                child: RankingContentView(
                  isLeftSelected: viewModel.isLeftSelected.value,
                ),
              ),
            ),
          ],
        ),
      );
    });
  }
}
