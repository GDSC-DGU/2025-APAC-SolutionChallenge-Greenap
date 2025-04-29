import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';

class VerificationScreen extends StatelessWidget {
  const VerificationScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: Center(child: Text('인증 페이지')),
    );
  }
}
