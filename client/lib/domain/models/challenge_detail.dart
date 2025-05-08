import 'challenge_item.dart';

class ChallengeDetailModel extends ChallengeItemModel {
  final String preDescription;
  final String certificationExampleImageUrl;
  final String certificationMethodDescription;
  final List<int> participationDates;
  final double percentOfCompletedUser;

  ChallengeDetailModel({
    required int id,
    required String title,
    required String description,
    required String mainImageUrl,
    required this.preDescription,
    required this.certificationExampleImageUrl,
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
      mainImageUrl: json['main_image_url'],
      preDescription: json['pre_description'],
      certificationExampleImageUrl: json['certification_example_image_url'],
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
