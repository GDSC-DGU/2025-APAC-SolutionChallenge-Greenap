import 'package:greenap/domain/models/challenge_detail.dart';

class ChallengeDetailDto {
  final int id;
  final String title;
  final String preDescription;
  final String description;
  final String mainImageUrl;
  final String certificationExampleImageUrl;
  final String certificationMethodDescription;
  final List<int> participationDates;
  final double percentOfCompletedUser;

  ChallengeDetailDto({
    required this.id,
    required this.title,
    required this.preDescription,
    required this.description,
    required this.mainImageUrl,
    required this.certificationExampleImageUrl,
    required this.certificationMethodDescription,
    required this.participationDates,
    required this.percentOfCompletedUser,
  });

  factory ChallengeDetailDto.fromJson(Map<String, dynamic> json) {
    final challenge = json['challenge'];
    return ChallengeDetailDto(
      id: challenge['id'],
      title: challenge['title'] ?? '',
      preDescription: challenge['pre_description'] ?? '',
      description: challenge['description'] ?? '',
      mainImageUrl: challenge['main_image_url'] ?? '',
      certificationExampleImageUrl:
          challenge['certification_example_image_url'] ?? '', // 임시
      certificationMethodDescription:
          challenge['certification_method_description'],
      participationDates:
          (challenge['participation_dates'] as List)
              .map((e) => int.parse(e.toString()))
              .toList(),
      percentOfCompletedUser:
          (challenge['percent_of_completed_user'] as num).toDouble(),
    );
  }

  ChallengeDetailModel toModel() {
    return ChallengeDetailModel(
      id: id,
      title: title,
      preDescription: preDescription,
      description: description,
      mainImageUrl: mainImageUrl,
      certificationExampleImageUrl: certificationExampleImageUrl,
      certificationMethodDescription: certificationMethodDescription,
      participationDates: participationDates,
      percentOfCompletedUser: percentOfCompletedUser,
    );
  }
}
