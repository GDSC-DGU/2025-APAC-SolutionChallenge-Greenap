import 'package:flutter/material.dart';
import 'verification_day_circle.dart';
import 'package:greenap/domain/enums/verification.dart';
import 'package:greenap/domain/extensions/verification_ext.dart';

class VerificationDayListView extends StatelessWidget {
  final int totalDays;
  final List<Map<String, dynamic>> certificationDataList;

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

    final Map<int, VerificationStatus> statusByDay = {};
    for (int i = 0; i < certificationDataList.length; i++) {
      final raw = certificationDataList[i];
      final dynamic statusRaw = raw['is_certificated'];
      final String statusStr =
          (statusRaw is String)
              ? statusRaw.toUpperCase()
              : 'FAILED'; // fallback
      final rawDate = raw['date'];
      DateTime? certDate;

      if (rawDate is String) {
        certDate = DateTime.tryParse(rawDate);
      } else if (rawDate is List && rawDate.length == 3) {
        certDate = DateTime(rawDate[0], rawDate[1], rawDate[2]);
      }
      final isToday =
          certDate != null &&
          certDate.year == DateTime.now().year &&
          certDate.month == DateTime.now().month &&
          certDate.day == DateTime.now().day;

      final status =
          isToday
              ? VerificationStatus.upcoming
              : VerificationStatusExtension.fromString(statusStr);

      statusByDay[i + 1] = status;
      print(statusByDay);
    }

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
              final status = statusByDay[day] ?? VerificationStatus.upcoming;

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
