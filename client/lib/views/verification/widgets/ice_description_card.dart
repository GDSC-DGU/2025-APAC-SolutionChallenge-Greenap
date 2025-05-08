import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:flutter_svg/flutter_svg.dart';

class IceDescriptionCard extends StatelessWidget {
  IceDescriptionCard({super.key});
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(4),
        color: ColorSystem.skyblue.withOpacity(0.05),
      ),
      padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Column(
        children: [
          Row(
            children: [
              SvgPicture.asset('assets/icons/ice.svg', width: 16, height: 16),
              SizedBox(width: 4),
              Text(
                "얼리기란?",
                style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
              ),
            ],
          ),
          SizedBox(height: 4),
          Text(
            "챌린지 참여일의 절반을 넘어갈 경우 1회 얼리기가 가능합니다! 얼리기 기능을 활용하여 챌린지 연속 일수를 지켜보아요",
            style: FontSystem.Body3.copyWith(color: ColorSystem.gray[700]),
          ),
        ],
      ),
    );
  }
}
