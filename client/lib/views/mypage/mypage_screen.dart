import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';

class MypageScreen extends StatelessWidget {
  const MypageScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: Center(child: Text('마이 페이지')),
    );
  }
}
