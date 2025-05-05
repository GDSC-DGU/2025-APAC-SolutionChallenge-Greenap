import 'package:flutter/material.dart';
import 'package:greenap/config/font_system.dart';
import './challenge_category_grid.dart';
import './challenge_encourage_message.dart';

class ChallengeCategory extends StatelessWidget {
  const ChallengeCategory({super.key});
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('챌린지 카테고리', style: FontSystem.Head2),
        SizedBox(height: 12),
        ChallengeEncourageMessage(message: 'AI 생성 독려 메시지가 들어갈 부분입니다!'),

        SizedBox(height: 12),
        ChallengeCategoryGrid(),
      ],
    );
  }
}
