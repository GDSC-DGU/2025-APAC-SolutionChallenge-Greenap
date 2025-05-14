class ChallengeReportModel {
  final int userChallengeId;
  final int totalDays;
  final String status;
  final int successDays;
  final String reportMessage;
  final int maxConsecutiveDays;
  final int ranking;
  final List<Map<String, String>> certificationDataList;

  ChallengeReportModel({
    required this.userChallengeId,
    required this.totalDays,
    required this.status,
    required this.successDays,
    required this.reportMessage,
    required this.maxConsecutiveDays,
    required this.ranking,
    required this.certificationDataList,
  });
}
