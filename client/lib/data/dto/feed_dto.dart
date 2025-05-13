import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/domain/models/user.dart';

class FeedItemDto {
  final String category;
  final String challengeTitle;
  final String imageUrl;
  final String content;
  final DateTime createdAt;
  final FeedUserDto user;

  FeedItemDto({
    required this.category,
    required this.challengeTitle,
    required this.imageUrl,
    required this.content,
    required this.createdAt,
    required this.user,
  });

  factory FeedItemDto.fromJson(Map<String, dynamic> json) {
    return FeedItemDto(
      category: json['category'] ?? '',
      challengeTitle: json['challenge_title'] ?? '',
      imageUrl: json['image_url'] ?? '',
      content: json['content'] ?? '',
      createdAt: DateTime.parse(json['createdAt']),
      user: FeedUserDto.fromJson(json['user']),
    );
  }

  FeedItemModel toModel() {
    return FeedItemModel(
      category: category,
      challengeTitle: challengeTitle,
      imageUrl: imageUrl,
      content: content,
      createdAt: createdAt,
      user: user.toModel(),
    );
  }
}

class FeedUserDto {
  final String nickname;
  final String profileImageUrl;
  final int burningLevel;

  FeedUserDto({
    required this.nickname,
    required this.profileImageUrl,
    required this.burningLevel,
  });

  factory FeedUserDto.fromJson(Map<String, dynamic> json) {
    return FeedUserDto(
      nickname: json['nickname'] ?? '',
      profileImageUrl: json['profileImageUrl'] ?? '',
      burningLevel: json['burningLevel'] ?? 0,
    );
  }

  UserModel toModel() {
    return UserModel(nickname: nickname, profileImageUrl: profileImageUrl);
  }
}
