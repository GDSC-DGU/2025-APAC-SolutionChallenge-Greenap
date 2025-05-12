import 'challenge_item.dart';

class ChallengeCategoryModel {
  final int id;
  final String title;
  final String description;
  final String imageUrl;
  final List<ChallengeItemModel> challenges;

  ChallengeCategoryModel({
    required this.id,
    required this.title,
    required this.description,
    required this.imageUrl,
    required this.challenges,
  });
}
