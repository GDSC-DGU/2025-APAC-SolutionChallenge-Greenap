import 'package:intl/intl.dart';

class CertificationDataDto {
  final String date;
  final String isCertificated;

  CertificationDataDto({required this.date, required this.isCertificated});

  factory CertificationDataDto.fromJson(Map<String, dynamic> json) {
    final List<dynamic> rawDate = json['date'];

    final date = DateTime(rawDate[0], rawDate[1], rawDate[2]);
    final formattedDate = DateFormat('yyyy-MM-dd').format(date);
    return CertificationDataDto(
      date: formattedDate,
      isCertificated: json['is_certificated'],
    );
  }
}
