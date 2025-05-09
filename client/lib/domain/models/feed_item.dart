import 'user.dart';

class FeedItemModel {
  final int id;
  final String category;
  final String challengeTitle;
  final String imageUrl;
  final String content;
  final DateTime createdAt;
  final UserModel user;

  FeedItemModel({
    required this.id,
    required this.category,
    required this.challengeTitle,
    required this.imageUrl,
    required this.content,
    required this.createdAt,
    required this.user,
  });

  factory FeedItemModel.fromJson(Map<String, dynamic> json) {
    return FeedItemModel(
      id: json['id'],
      category: json['category'],
      challengeTitle: json['challenge_title'],
      imageUrl: json['image_url'],
      content: json['content'],
      createdAt: DateTime.parse(json['createdAt']),
      user: UserModel.fromJson(json['user']),
    );
  }
}
