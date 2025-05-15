import 'package:greenap/domain/models/all_ranking.dart';

class UserRankDto {
  final String nickname;
  final String profileImageUrl;
  final int? longestConsecutiveParticipationCount;
  final int? totalParticipationCount;

  UserRankDto({
    required this.nickname,
    required this.profileImageUrl,
    required this.longestConsecutiveParticipationCount,
    required this.totalParticipationCount,
  });

  factory UserRankDto.fromJson(Map<String, dynamic> json) {
    return UserRankDto(
      nickname: json['nickname'],
      profileImageUrl: json['profile_image_url'],
      longestConsecutiveParticipationCount:
          json['longest_consecutive_participation_count'],
      totalParticipationCount: json['total_participation_count'],
    );
  }
  ParticipantUserModel toModel() {
    return ParticipantUserModel(
      nickname: nickname,
      profileImageUrl: profileImageUrl,
      longestConsecutiveParticipationCount:
          longestConsecutiveParticipationCount,
      totalParticipationCount: totalParticipationCount,
    );
  }
}

class MyRankDto {
  final int? rank;
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

class MyChallengeRankingDto {
  final String challengeTitle;
  final MyRankDto userRankInfo;

  MyChallengeRankingDto({
    required this.challengeTitle,
    required this.userRankInfo,
  });

  factory MyChallengeRankingDto.fromJson(Map<String, dynamic> json) {
    return MyChallengeRankingDto(
      challengeTitle: json['challenge_title'],
      userRankInfo: MyRankDto.fromJson(json['user_rank_info']),
    );
  }

  /// 모델 변환 함수 (뷰모델에서 사용)
  MyChallengeRankingModel toModel() {
    return MyChallengeRankingModel(
      challengeTitle: challengeTitle,
      userRankInfo: userRankInfo.toModel(),
    );
  }
}
