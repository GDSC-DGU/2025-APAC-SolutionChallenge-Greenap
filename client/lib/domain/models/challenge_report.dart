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

  factory ChallengeReportModel.fromJson(Map<String, dynamic> json) {
    return ChallengeReportModel(
      userChallengeId: json['user_challenge_id'],
      totalDays: json['total_days'],
      status: json['user_challenge_status'],
      successDays: json['success_days'],
      reportMessage: json['report_message'],
      maxConsecutiveDays: json['max_consecutive_participation_days'],
      ranking: json['challenge_ranking'],
      certificationDataList: List<Map<String, String>>.from(
        (json['certification_data_list'] as List).map(
          (item) => Map<String, String>.from(item),
        ),
      ),
    );
  }
}
