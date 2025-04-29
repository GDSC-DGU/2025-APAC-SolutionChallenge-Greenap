import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:flutter_svg/flutter_svg.dart';

class ChallengeEncourageMessage extends StatelessWidget {
  final String message;
  const ChallengeEncourageMessage({super.key, required this.message});
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
            child: Text(
              '$message',
              style: FontSystem.Body3.copyWith(color: ColorSystem.gray[700]),
            ),
          ),
        ],
      ),
    );
  }
}
