import 'challenge_item.dart';

class ChallengeDetailModel extends ChallengeItemModel {
  final String preDescription;
  final String certificationMethodDescription;
  final List<int> participationDates;
  final double percentOfCompletedUser;

  ChallengeDetailModel({
    required int id,
    required String title,
    required String description,
    required String mainImageUrl,
    required this.preDescription,
    required this.certificationMethodDescription,
    required this.participationDates,
    required this.percentOfCompletedUser,
  }) : super(
         id: id,
         title: title,
         description: description,
         mainImageUrl: mainImageUrl,
       );

  factory ChallengeDetailModel.fromJson(Map<String, dynamic> json) {
    return ChallengeDetailModel(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      mainImageUrl:
          json['image_url'], // 상세 API는 `mainImageUrl` 대신 `image_url`을 쓰므로 여기에 맞춤
      preDescription: json['pre_description'],
      certificationMethodDescription: json['certification_method_description'],
      participationDates:
          (json['participation_dates'] as List)
              .map((e) => int.parse(e.toString()))
              .toList(),
      percentOfCompletedUser: double.parse(
        (json['percent_of_completed_user']).toStringAsFixed(2),
      ),
    );
  }
}
