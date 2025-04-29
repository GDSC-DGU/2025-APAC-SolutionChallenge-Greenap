import 'package:flutter/material.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/config/color_system.dart';
import 'widgets/challenge_category/challenge_category.dart';
import 'widgets/user_info/user_info.dart';
import 'widgets/today_habit_card/today_habit_card.dart';
import 'package:greenap/views_model/home_view_model.dart';
import 'package:get/get.dart';

class HomeScreen extends GetView<HomeViewModel> {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: SingleChildScrollView(
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
                  TodayHabitCard(
                    challenges: [
                      {'name': '텀블러 사용하기', 'isCerficated': true},
                      {'name': '대중교통 이용하기', 'isCerficated': false},
                      {'name': '채식 식단 실천하기', 'isCerficated': false},
                    ],
                  ),
                ],
              ),
              const SizedBox(height: 32),
              ChallengeCategory(),
            ],
          ),
        ),
      ),
    );
  }
}
