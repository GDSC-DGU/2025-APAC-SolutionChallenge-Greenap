import 'all_ranking_dto.dart';
import 'package:greenap/domain/models/challenge_ranking.dart';

class ChallengeRankingDto {
  final String challengeTitle;
  final int totalParticipantsCount;
  final List<Top100ParticipantDto> top100Participants;

  ChallengeRankingDto({
    required this.challengeTitle,
    required this.totalParticipantsCount,
    required this.top100Participants,
  });

  factory ChallengeRankingDto.fromJson(Map<String, dynamic> json) =>
      ChallengeRankingDto(
        challengeTitle: json['challenge_title'],
        totalParticipantsCount: json['total_participants_count'],
        top100Participants:
            (json['top_100_participants'] as List)
                .map((e) => Top100ParticipantDto.fromJson(e))
                .toList(),
      );
  ChallengeRankingModel toModel() {
    return ChallengeRankingModel(
      challengeTitle: challengeTitle,
      totalParticipantsCount: totalParticipantsCount,
      top100Participants: top100Participants.map((e) => e.toModel()).toList(),
    );
  }
}
