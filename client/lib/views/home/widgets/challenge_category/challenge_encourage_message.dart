import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/home/home_view_model.dart';

class ChallengeEncourageMessage extends StatelessWidget {
  ChallengeEncourageMessage({super.key});
  final viewModel = Get.find<HomeViewModel>();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(4),
        color: ColorSystem.white,
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.1),
            blurRadius: 4,
            offset: Offset(0, 0),
          ),
        ],
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          SvgPicture.asset('assets/icons/earth.svg', width: 20, height: 20),
          SizedBox(width: 6),
          Expanded(
            child: Obx(
              () => Text(
                viewModel.encourageMessage.value ?? '오늘도 친환경 도전!',
                style: FontSystem.Body3.copyWith(color: ColorSystem.gray[700]),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
