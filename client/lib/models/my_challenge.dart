import 'package:greenap/enums/challenge.dart';

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
      challengeId: json['challengeId'],
      title: json['title'],
      category: json['category'],
      totalDays: json['totalDays'],
      elapsedDays: json['elapsedDays'],
      progress: json['progress'],
      iceCount: json['iceCount'],
      isCerficatedInToday: json['isCerficatedInToday'],
      status: json['status'],
      certificationDataList: json['certificationDataList'],
    );
  }
}
