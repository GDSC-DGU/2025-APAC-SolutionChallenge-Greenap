import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/mypage/mypage_view_model.dart';

class MypageScreen extends GetView<MypageViewModel> {
  const MypageScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: Center(child: Text('마이 페이지')),
    );
  }
}
