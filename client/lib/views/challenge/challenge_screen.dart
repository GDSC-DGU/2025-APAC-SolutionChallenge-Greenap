import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';

class ChallengeScreen extends StatelessWidget {
  const ChallengeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: Center(child: Text('챌린지 페이지')),
    );
  }
}
