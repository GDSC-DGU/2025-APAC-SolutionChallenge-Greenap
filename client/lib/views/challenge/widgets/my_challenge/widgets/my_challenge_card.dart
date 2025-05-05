import 'package:flutter/material.dart';
import 'package:greenap/models/challenge.dart';
import 'package:greenap/enums/challenge.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

class ChallengeCard extends StatelessWidget {
  final ChallengeModel challenge;

  const ChallengeCard({super.key, required this.challenge});

  Color getStatusColor(ChallengeStatus status) {
    switch (status) {
      case ChallengeStatus.RUNNING:
        return ColorSystem.mint!;
      case ChallengeStatus.COMPLETED:
        return Colors.grey;
    }
  }

  String getStatusLabel(ChallengeStatus status) {
    switch (status) {
      case ChallengeStatus.RUNNING:
        return "진행중";
      case ChallengeStatus.COMPLETED:
        return "진행완료";
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      color: ColorSystem.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(challenge.title, style: FontSystem.Body1Bold),
            const SizedBox(height: 12),
            Chip(
              label: Text(getStatusLabel(challenge.status)),
              backgroundColor: getStatusColor(
                challenge.status,
              ).withOpacity(0.1),
              labelStyle: TextStyle(color: getStatusColor(challenge.status)),
            ),
          ],
        ),
      ),
    );
  }
}
