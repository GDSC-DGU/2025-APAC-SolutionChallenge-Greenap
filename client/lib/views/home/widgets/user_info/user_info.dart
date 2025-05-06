import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:flutter_svg/svg.dart';

class UserInfo extends StatelessWidget {
  const UserInfo({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 0, vertical: 12),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            "지속 가능한 하루, OOO님과 함께",
            style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
          ),
          Row(
            children: [
              SvgPicture.asset('assets/icons/earth.svg', width: 24, height: 24),
              const SizedBox(width: 4),
              Text(
                "15",
                style: FontSystem.Head3.copyWith(color: ColorSystem.mint),
              ),
              const SizedBox(width: 2),
              Text(
                "days",
                style: FontSystem.Head3.copyWith(color: ColorSystem.gray[800]),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
