import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:flutter_svg/svg.dart';
import 'package:greenap/config/font_system.dart';

class ChallengeItem extends StatelessWidget {
  final String challengeName;
  final bool isCerficated;

  const ChallengeItem({
    Key? key,
    required this.challengeName,
    required this.isCerficated,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final completeColor = ColorSystem.mint!;
    final uncompleteColor = ColorSystem.gray[500]!;

    return Container(
      padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),

      decoration: BoxDecoration(
        color: ColorSystem.white,
        borderRadius: BorderRadius.circular(4),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.04),
            spreadRadius: 2, // 그림자 퍼지는 정도
            blurRadius: 4, // 그림자 흐림 정도
            offset: Offset(0, 0), // 그림자 위치 (x축, y축)
          ),
        ],
      ),
      child: Row(
        children: [
          SvgPicture.asset(
            'assets/icons/check.svg',
            width: 16,
            height: 16,
            colorFilter: ColorFilter.mode(
              isCerficated ? completeColor : uncompleteColor,
              BlendMode.srcIn,
            ),
          ),
          const SizedBox(width: 6),
          Text('$challengeName', style: FontSystem.Body3),
        ],
      ),
    );
  }
}
