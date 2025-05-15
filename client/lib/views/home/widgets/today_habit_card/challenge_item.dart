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
    final backgroundColor = ColorSystem.white;
    final textColor = ColorSystem.gray[700]!;
    final completeIconColor = ColorSystem.mint;
    final unCompleteIconColor = ColorSystem.gray[700]!;

    return Container(
      padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),

      decoration: BoxDecoration(
        color: backgroundColor,

        borderRadius: BorderRadius.circular(4),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.04),
            spreadRadius: 2,
            blurRadius: 4,
            offset: Offset(0, 0),
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
              isCerficated ? completeIconColor : unCompleteIconColor,
              BlendMode.srcIn,
            ),
          ),
          const SizedBox(width: 6),
          Text(
            '$challengeName',
            style: FontSystem.Body3.copyWith(color: textColor),
          ),
        ],
      ),
    );
  }
}
