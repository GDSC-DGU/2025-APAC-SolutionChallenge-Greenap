import 'package:greenap/domain/models/challenge_item.dart';
import 'package:greenap/domain/models/challenge_category.dart';

class ChallengeItemDto {
  final int id;
  final String title;
  final String preDescription;
  final String mainImageUrl;

  ChallengeItemDto({
    required this.id,
    required this.title,
    required this.preDescription,
    required this.mainImageUrl,
  });

  factory ChallengeItemDto.fromJson(Map<String, dynamic> json) {
    return ChallengeItemDto(
      id: json['id'],
      title: json['title'],
      preDescription: json['preDescription'],
      mainImageUrl: json['main_image_url'],
    );
  }

  ChallengeItemModel toModel() {
    return ChallengeItemModel(
      id: id,
      title: title,
      description: preDescription,
      mainImageUrl: mainImageUrl,
    );
  }
}

class ChallengeCategoryDto {
  final int id;
  final String title;
  final String description;
  final String imageUrl;
  final List<ChallengeItemDto> challenges;

  ChallengeCategoryDto({
    required this.id,
    required this.title,
    required this.description,
    required this.imageUrl,
    required this.challenges,
  });

  factory ChallengeCategoryDto.fromJson(Map<String, dynamic> json) {
    return ChallengeCategoryDto(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      imageUrl: json['image_url'],
      challenges:
          (json['challenges'] as List)
              .map((e) => ChallengeItemDto.fromJson(e))
              .toList(),
    );
  }

  ChallengeCategoryModel toModel() {
    return ChallengeCategoryModel(
      id: id,
      title: title,
      description: description,
      imageUrl: imageUrl,
      challenges: challenges.map((e) => e.toModel()).toList(),
    );
  }
}
