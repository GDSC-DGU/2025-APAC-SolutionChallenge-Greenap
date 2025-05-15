import 'package:flutter/material.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import './widgets/category_filter.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/challenge/my_challenge_view_model.dart';
import './widgets//my_challenge_card.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/domain/extensions/challenge_ext.dart';

class MyChallengeView extends BaseScreen<MyChallengeViewModel> {
  MyChallengeView({super.key});

  @override
  bool get applyTopSafeArea => false;

  @override
  Widget buildBody(BuildContext context) {
    return Column(
      children: [
        CategoryFilter(),
        const SizedBox(height: 24),
        Expanded(
          child: Obx(() {
            final filterSet = viewModel.status.toChallengeStatus();
            final filtered =
                viewModel.myChallenges.where((challenge) {
                  if (filterSet == null) return true;
                  return filterSet.contains(challenge.status);
                }).toList();

            return _buildChallengeList(filtered);
          }),
        ),
      ],
    );
  }

  Widget _buildChallengeList(List<MyChallengeModel> filtered) {
    return ListView.separated(
      itemCount: filtered.length,
      separatorBuilder: (_, __) => const SizedBox(height: 20),
      itemBuilder: (context, index) {
        return MyChallengeCard(myChallenge: filtered[index]);
      },
    );
  }
}
