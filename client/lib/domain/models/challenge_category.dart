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

  factory ChallengeCategoryModel.fromJson(Map<String, dynamic> json) {
    return ChallengeCategoryModel(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      imageUrl: json['image_url'],
      challenges:
          (json['challenges'] as List)
              .map((e) => ChallengeItemModel.fromJson(e))
              .toList(),
    );
  }
}
