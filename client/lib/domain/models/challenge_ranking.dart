import 'all_ranking.dart';

class ChallengeRankingModel {
  final String challengeTitle;
  final int totalParticipantsCount;
  final List<ParticipantModel> top100Participants;

  ChallengeRankingModel({
    required this.challengeTitle,
    required this.totalParticipantsCount,
    required this.top100Participants,
  });
}
