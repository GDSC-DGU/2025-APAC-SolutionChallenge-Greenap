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
      isCerficatedInToday: ChallengeCertificated.values.firstWhere(
        (e) =>
            e.name == json['is_cerficated_in_today'].toString().toLowerCase(),
        orElse: () => ChallengeCertificated.FAILED,
      ),
      status: ChallengeStatus.values.firstWhere(
        (e) => e.name == json['status'].toString().toLowerCase(),
        orElse: () => ChallengeStatus.running,
      ),
      certificationDataList: List<Map<String, String>>.from(
        (json['certification_data_list'] as List).map(
          (item) => Map<String, String>.from(item),
        ),
      ),
    );
  }
}
