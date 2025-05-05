import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/verification/verification_view_model.dart';

class VerificationScreen extends GetView<VerificationViewModel> {
  const VerificationScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: Center(child: Text('인증 페이지')),
    );
  }
}
