import 'package:flutter/material.dart';
import '../enums/verification.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

extension VerificationStatusExtension on VerificationStatus {
  static VerificationStatus fromString(String value) {
    switch (value.toLowerCase()) {
      case 'true':
        return VerificationStatus.success;
      case 'false':
        return VerificationStatus.failed;
      case 'ice':
        return VerificationStatus.ice;
      default:
        return VerificationStatus.failed;
    }
  }

  Color get color {
    switch (this) {
      case VerificationStatus.success:
        return ColorSystem.mint;
      case VerificationStatus.failed:
        return Colors.redAccent;
      case VerificationStatus.ice:
        return Colors.blueAccent;
      case VerificationStatus.upcoming:
        return ColorSystem.gray[300]!;
    }
  }

  Widget iconOrText(int dayNumber) {
    switch (this) {
      case VerificationStatus.success:
        return SvgPicture.asset(
          'assets/icons/checked.svg',
          width: 39,
          height: 39,
          fit: BoxFit.contain,
        );
      case VerificationStatus.failed:
        return SvgPicture.asset(
          'assets/icons/failed.svg',
          width: 40,
          height: 40,
          fit: BoxFit.contain,
        );
      case VerificationStatus.ice:
      case VerificationStatus.upcoming:
        return Stack(
          alignment: Alignment.center,
          children: [
            SvgPicture.asset(
              'assets/icons/upcoming.svg',
              width: 40,
              height: 40,
            ),
            Text(
              '$dayNumber',
              style: FontSystem.Body2Bold.copyWith(
                color: ColorSystem.gray[300],
              ),
            ),
          ],
        );
    }
  }
}
