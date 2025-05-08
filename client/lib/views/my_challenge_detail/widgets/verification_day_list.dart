import 'package:flutter/material.dart';
import 'verification_day_circle.dart';
import 'package:greenap/domain/enums/verification.dart';
import 'package:greenap/domain/extensions/verification_ext.dart';
import 'package:greenap/config/color_system.dart';

class VerificationDayListView extends StatelessWidget {
  final int totalDays;
  final List<Map<String, String>> certificationDataList;

  const VerificationDayListView({
    super.key,
    required this.totalDays,
    required this.certificationDataList,
  });

  @override
  Widget build(BuildContext context) {
    const int itemsPerRow = 5;
    const double spacing = 12;
    final double horizontalPadding = 20.0;

    return LayoutBuilder(
      builder: (context, constraints) {
        final totalSpacing = spacing * (itemsPerRow - 1);
        final itemWidth =
            (constraints.maxWidth - 2 * horizontalPadding - totalSpacing) /
            itemsPerRow;

        return Container(
          margin: const EdgeInsets.symmetric(horizontal: 0),
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(
            color: Colors.white,
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                blurRadius: 4,
                spreadRadius: 1,
                offset: const Offset(0, 2),
              ),
            ],
            borderRadius: BorderRadius.circular(8),
          ),
          child: Wrap(
            spacing: spacing,
            runSpacing: spacing,
            children: List.generate(totalDays, (index) {
              final day = index + 1;

              VerificationStatus status;
              if (index < certificationDataList.length) {
                final rawStatus =
                    certificationDataList[index]['is_certificated'] ?? 'false';
                status = VerificationStatusExtension.fromString(rawStatus);
              } else {
                status = VerificationStatus.upcoming;
              }
              return SizedBox(
                width: itemWidth,
                child: VerificationDayCircle(dayNumber: day, status: status),
              );
            }),
          ),
        );
      },
    );
  }
}
