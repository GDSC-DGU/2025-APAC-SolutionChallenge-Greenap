import 'package:flutter/material.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/config/color_system.dart';
import 'widgets/challenge_category/challenge_category.dart';
import 'widgets/user_info/user_info.dart';
import 'widgets/today_habit_card/today_habit_card.dart';
import 'package:greenap/views_model/home/home_view_model.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:get/get.dart';
import 'package:greenap/domain/enums/challenge.dart';

class HomeScreen extends BaseScreen<HomeViewModel> {
  const HomeScreen({super.key});

  @override
  bool get applyTopSafeArea => true;

  @override
  Widget buildBody(BuildContext context) {
    return SingleChildScrollView(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            UserInfo(),
            const SizedBox(height: 24),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '오늘의 Habit',
                  style: FontSystem.Head1.copyWith(color: ColorSystem.mint),
                ),
                const SizedBox(height: 12),
                Obx(() {
                  final challengeList = viewModel.myChallengeList;
                  return TodayHabitCard(
                    challenges:
                        challengeList.map((challenge) {
                          return {
                            'name': challenge.title,
                            'isCerficated':
                                challenge.isCerficatedInToday ==
                                        ChallengeCertificated.SUCCESS
                                    ? true
                                    : false,
                          };
                        }).toList(),
                  );
                }),
              ],
            ),
            const SizedBox(height: 32),
            ChallengeCategory(),
          ],
        ),
      ),
    );
  }
}
