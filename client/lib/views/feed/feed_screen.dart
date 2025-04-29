import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';

class FeedScreen extends StatelessWidget {
  const FeedScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: Center(child: Text('피드 페이지')),
    );
  }
}
