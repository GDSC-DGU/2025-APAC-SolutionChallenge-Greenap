import 'package:greenap/domain/models/all_ranking.dart';

class AllRankingDto {
  final int totalParticipantsCount;
  final List<Top100ParticipantDto> top100Participants;

  AllRankingDto({
    required this.totalParticipantsCount,
    required this.top100Participants,
  });

  factory AllRankingDto.fromJson(Map<String, dynamic> json) {
    return AllRankingDto(
      totalParticipantsCount: json['total_participants_count'] ?? 0,
      top100Participants:
          (json['top_100_participants'] as List)
              .map((e) => Top100ParticipantDto.fromJson(e))
              .toList(),
    );
  }
  RankingModel toModel() {
    return RankingModel(
      totalParticipantsCount: totalParticipantsCount,
      top100Participants: top100Participants.map((e) => e.toModel()).toList(),
    );
  }
}

class Top100ParticipantDto {
  final int rank;
  final ParticipantUserDto user;

  Top100ParticipantDto({required this.rank, required this.user});

  factory Top100ParticipantDto.fromJson(Map<String, dynamic> json) {
    return Top100ParticipantDto(
      rank: json['rank'] ?? 0,
      user: ParticipantUserDto.fromJson(json['user']),
    );
  }

  ParticipantModel toModel() {
    return ParticipantModel(rank: rank, user: user.toModel());
  }
}

class ParticipantUserDto {
  final String nickname;
  final String profileImageUrl;
  final int longestConsecutiveParticipationCount;

  ParticipantUserDto({
    required this.nickname,
    required this.profileImageUrl,
    required this.longestConsecutiveParticipationCount,
  });

  factory ParticipantUserDto.fromJson(Map<String, dynamic> json) {
    return ParticipantUserDto(
      nickname: json['nickname'] ?? '',
      profileImageUrl: json['profile_image_url'] ?? '',
      longestConsecutiveParticipationCount:
          json['longest_consecutive_participation_count'] ?? 0,
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
