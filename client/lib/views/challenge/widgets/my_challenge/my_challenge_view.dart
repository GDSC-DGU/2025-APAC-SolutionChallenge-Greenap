import 'package:flutter/material.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import './widgets/category_filter.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/challenge/my_challenge_view_model.dart';
import 'package:greenap/domain/models/dummy/my_challenge_dummy.dart';
import './widgets//my_challenge_card.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/domain/models/dummy/challenge_dummy.dart';

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
            final filtered =
                dummyMyChallenges
                    .where(
                      (item) => isStatusMatch(item, viewModel.status.value),
                    )
                    .toList();

            return _buildChallengeList(filtered);
          }),
        ),
      ],
    );
  }

  bool isStatusMatch(
    MyChallengeModel item,
    ChallengeFilterStatus filterStatus,
  ) {
    if (filterStatus == ChallengeFilterStatus.all) return true;

    final statusToCompare =
        (filterStatus == ChallengeFilterStatus.running)
            ? ChallengeStatus.running
            : ChallengeStatus.completed;

    return item.status == statusToCompare;
  }

  Widget _buildChallengeList(List<MyChallengeModel> filtered) {
    final allChallenges = dummyChallengeCategory;
    return ListView.separated(
      itemCount: filtered.length,
      separatorBuilder: (_, __) => const SizedBox(height: 20),
      itemBuilder: (context, index) {
        return MyChallengeCard(
          myChallenge: filtered[index],
          allChallenges: allChallenges,
        );
      },
    );
  }
}
