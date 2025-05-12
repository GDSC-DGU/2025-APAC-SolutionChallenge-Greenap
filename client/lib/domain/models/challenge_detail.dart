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
}
