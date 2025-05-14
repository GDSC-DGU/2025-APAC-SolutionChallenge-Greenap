import 'package:greenap/data/dto/certification_data_dto.dart';
import 'package:greenap/domain/models/challenge_report.dart';

class ChallengeReportDto {
  final int userChallengeId;
  final int totalDays;
  final String userChallengeStatus;
  final int successDays;
  final String reportMessage;
  final int maxConsecutiveParticipationDays;
  final int challengeRanking;
  final List<CertificationDataDto> certificationDataList;

  ChallengeReportDto({
    required this.userChallengeId,
    required this.totalDays,
    required this.userChallengeStatus,
    required this.successDays,
    required this.reportMessage,
    required this.maxConsecutiveParticipationDays,
    required this.challengeRanking,
    required this.certificationDataList,
  });

  factory ChallengeReportDto.fromJson(Map<String, dynamic> json) {
    return ChallengeReportDto(
      userChallengeId: json['user_challenge_id'],
      totalDays: json['total_days'],
      userChallengeStatus: json['user_challenge_status'],
      successDays: json['success_days'],
      reportMessage: json['report_message'],
      maxConsecutiveParticipationDays:
          json['max_consecutive_participation_days'],
      challengeRanking: json['challenge_ranking'],
      certificationDataList:
          (json['certification_data_list'] as List)
              .map((e) => CertificationDataDto.fromJson(e))
              .toList(),
    );
  }

  ChallengeReportModel toModel() {
    return ChallengeReportModel(
      userChallengeId: userChallengeId,
      totalDays: totalDays,
      status: userChallengeStatus,
      successDays: successDays,
      reportMessage: reportMessage,
      maxConsecutiveDays: maxConsecutiveParticipationDays,
      ranking: challengeRanking,
      certificationDataList:
          certificationDataList
              .map((e) => {'date': e.date, 'is_certificated': e.isCertificated})
              .toList(),
    );
  }
}
