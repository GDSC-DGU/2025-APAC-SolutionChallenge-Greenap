import 'package:greenap/domain/enums/challenge.dart';

class MyChallengeModel {
  final int id;
  final int challengeId;
  final String title;
  final String category;
  final int totalDays;
  final int elapsedDays;
  final int progress;
  final int iceCount;
  final ChallengeCertificated isCerficatedInToday;
  final ChallengeStatus status;
  final List certificationDataList;

  MyChallengeModel({
    required this.id,
    required this.challengeId,
    required this.title,
    required this.category,
    required this.totalDays,
    required this.elapsedDays,
    required this.progress,
    required this.iceCount,
    required this.isCerficatedInToday,
    required this.status,
    required this.certificationDataList,
  });

  factory MyChallengeModel.fromJson(Map<String, dynamic> json) {
    return MyChallengeModel(
      id: json['id'],
      challengeId: json['challenge_id'],
      title: json['title'],
      category: json['category'],
      totalDays: json['total_days'],
      elapsedDays: json['elapsed_days'],
      progress: json['progress'],
      iceCount: json['ice_count'],
      isCerficatedInToday: json['is_cerficated_in_today'],
      status: json['status'],
      certificationDataList: json['certification_data_list'],
    );
  }
}
