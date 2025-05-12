class CertificationDataDto {
  final String date;
  final String isCertificated;

  CertificationDataDto({required this.date, required this.isCertificated});

  factory CertificationDataDto.fromJson(Map<String, dynamic> json) {
    return CertificationDataDto(
      date: json['date'],
      isCertificated: json['is_certificated'],
    );
  }
}
