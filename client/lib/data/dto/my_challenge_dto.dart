import './certification_data_dto.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';

class MyChallengeDto {
  final int id;
  final int challengeId;
  final String title;
  final String category;
  final String status;
  final int totalDays;
  final int elapsedDays;
  final int progress;
  final int iceCount;
  final String isCertificatedInToday;
  final String mainImageUrl;
  final List<CertificationDataDto> certificationDataList;

  MyChallengeDto({
    required this.id,
    required this.challengeId,
    required this.title,
    required this.category,
    required this.status,
    required this.totalDays,
    required this.elapsedDays,
    required this.progress,
    required this.iceCount,
    required this.isCertificatedInToday,
    required this.mainImageUrl,
    required this.certificationDataList,
  });

  factory MyChallengeDto.fromJson(Map<String, dynamic> json) {
    return MyChallengeDto(
      id: json['id'],
      challengeId: json['challenge_id'],
      title: json['title'],
      category: json['category'],
      status: json['status'],
      totalDays: json['total_days'],
      elapsedDays: json['elapsed_days'],
      progress: json['progress'],
      iceCount: json['ice_count'],
      isCertificatedInToday: json['is_certificated_in_today'],
      mainImageUrl: json['main_image_url'],
      certificationDataList:
          (json['certification_data_list'] as List)
              .map((e) => CertificationDataDto.fromJson(e))
              .toList(),
    );
  }
  MyChallengeModel toModel({String? mainImageUrl}) {
    return MyChallengeModel(
      id: id,
      challengeId: challengeId,
      title: title,
      category: category,
      totalDays: totalDays,
      elapsedDays: elapsedDays,
      progress: progress,
      iceCount: iceCount,
      isCerficatedInToday: ChallengeCertificated.values.firstWhere(
        (e) => e.name.toUpperCase() == isCertificatedInToday.toUpperCase(),
        orElse: () => ChallengeCertificated.FAILED,
      ),
      status: ChallengeStatus.values.firstWhere(
        (e) => e.name.toUpperCase() == status.toUpperCase(),
        orElse: () => ChallengeStatus.running,
      ),
      certificationDataList:
          certificationDataList
              .map((e) => {'date': e.date, 'is_certificated': e.isCertificated})
              .toList(),
      mainImageUrl: mainImageUrl,
    );
  }
}
