import 'package:flutter/material.dart';
import 'package:greenap/domain/enums/verification.dart';
import 'package:greenap/domain/extensions/verification_ext.dart';

class VerificationDayCircle extends StatelessWidget {
  final int dayNumber;
  final VerificationStatus status;

  const VerificationDayCircle({
    super.key,
    required this.dayNumber,
    required this.status,
  });

  @override
  Widget build(BuildContext context) {
    return status.iconOrText(dayNumber);
  }
}
