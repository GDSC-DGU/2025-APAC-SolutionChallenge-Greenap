import 'package:flutter/material.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/config/color_system.dart';
import './challenge_category_grid.dart';
import './challenge_encourage_message.dart';

class ChallengeCategory extends StatelessWidget {
  ChallengeCategory({super.key});
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text(
          '챌린지 카테고리',
          style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
        ),
        SizedBox(height: 12),
        ChallengeEncourageMessage(),

        SizedBox(height: 12),
        ChallengeCategoryGrid(),
      ],
    );
  }
}
