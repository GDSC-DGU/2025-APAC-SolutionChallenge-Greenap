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
  final List<Map<String, String>> certificationDataList;
  final String? mainImageUrl;

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
    this.mainImageUrl,
  });
}
