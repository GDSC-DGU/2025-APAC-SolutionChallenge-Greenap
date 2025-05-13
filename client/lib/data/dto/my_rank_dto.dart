import 'package:greenap/domain/models/all_ranking.dart';

class UserRankDto {
  final String nickname;
  final String profileImageUrl;
  final int longestConsecutiveParticipationCount;

  UserRankDto({
    required this.nickname,
    required this.profileImageUrl,
    required this.longestConsecutiveParticipationCount,
  });

  factory UserRankDto.fromJson(Map<String, dynamic> json) {
    return UserRankDto(
      nickname: json['nickname'],
      profileImageUrl: json['profile_image_url'],
      longestConsecutiveParticipationCount:
          json['longest_consecutive_participation_count'],
    );
  }
  ParticipantUserModel toModel() {
    return ParticipantUserModel(
      nickname: nickname,
      profileImageUrl: profileImageUrl,
      longestConsecutiveParticipationCount:
          longestConsecutiveParticipationCount,
    );
  }
}

class MyRankDto {
  final int rank;
  final UserRankDto user;

  MyRankDto({required this.rank, required this.user});

  factory MyRankDto.fromJson(Map<String, dynamic> json) {
    return MyRankDto(
      rank: json['rank'],
      user: UserRankDto.fromJson(json['user']),
    );
  }

  ParticipantModel toModel() {
    return ParticipantModel(rank: rank, user: user.toModel());
  }
}
