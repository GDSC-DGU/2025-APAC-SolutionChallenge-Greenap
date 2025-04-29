import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'challenge_item.dart';

class TodayHabitCard extends StatelessWidget {
  final List<Map<String, dynamic>> challenges;

  const TodayHabitCard({super.key, required this.challenges});
  int get completedCount =>
      challenges.where((c) => c['isCerficated'] == true).length;

  double get completionRate {
    if (challenges.isEmpty) {
      return 0;
    }
    return completedCount / challenges.length;
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Positioned.fill(
          child: Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                  color: ColorSystem.black.withOpacity(0.08),
                  spreadRadius: 2,
                  blurRadius: 4,
                  offset: Offset(0, 0),
                ),
              ],
            ),
          ),
        ),

        Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(
            color: ColorSystem.mint.withOpacity(0.08),
            borderRadius: BorderRadius.circular(16),
          ),

          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text("2025.4.10 (목)", style: FontSystem.Caption),
              const SizedBox(height: 4),
              Text(
                '${challenges.length}개의 챌린지 중 ${completedCount}개의 챌린지를 완료했어요!',
                style: FontSystem.Head3,
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(flex: 2, child: _buildChallengeList()),

                  const SizedBox(width: 16),
                  Expanded(flex: 1, child: _buildProgressIndicator()),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildChallengeList() {
    return Column(
      children: List.generate(challenges.length, (index) {
        final challenge = challenges[index];
        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 6),
          child: ChallengeItem(
            challengeName: challenge['name'],
            isCerficated: challenge['isCerficated'],
          ),
        );
      }),
    );
  }

  Widget _buildProgressIndicator() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        SizedBox(
          child: Stack(
            alignment: Alignment.center,
            children: [
              SizedBox(
                width: 100,
                height: 100,
                child: CircularProgressIndicator(
                  value: completionRate,
                  strokeWidth: 10,
                  backgroundColor: ColorSystem.mint.withOpacity(0.2),
                  color: ColorSystem.mint,
                ),
              ),
              Column(
                children: [
                  Text('Completed', style: FontSystem.Body3),

                  Text(
                    '${(completionRate * 100).toInt()}%',
                    style: FontSystem.Body1Bold,
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }
}
