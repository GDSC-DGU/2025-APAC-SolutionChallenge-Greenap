import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/models/challenge.dart';
import './widgets/category_filter.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/challenge/my_challenge_view_model.dart';
import 'package:greenap/models/dummy/challenge_dummy.dart';
import './widgets//my_challenge_card.dart';
import 'package:greenap/enums/challenge.dart';

class MyChallengeView extends StatelessWidget {
  MyChallengeView({super.key}) {
    Get.put(MyChallengeViewModel());
  }

  @override
  Widget build(BuildContext context) {
    final controller = Get.find<MyChallengeViewModel>();

    return Column(
      children: [
        CategoryFilter(),
        const SizedBox(height: 24),
        Expanded(
          child: Obx(() {
            final filtered =
                controller.status.value == ChallengeFilterStatus.all
                    ? dummyChallenges
                    : dummyChallenges
                        .where((item) => item.status == controller.status.value)
                        .toList();

            return _buildChallengeList(filtered);
          }),
        ),
      ],
    );
  }

  Widget _buildChallengeList(List<ChallengeModel> filtered) {
    return ListView.separated(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
      itemCount: dummyChallenges.length,
      separatorBuilder: (_, __) => const SizedBox(height: 12),
      itemBuilder: (context, index) {
        return ChallengeCard(challenge: dummyChallenges[index]);
      },
    );
  }
}
