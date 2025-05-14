import 'package:greenap/domain/models/all_ranking.dart';

class TotalParticipantUserModel {
  final String nickname;
  final String profileImageUrl;
  final int totalParticipationCount;

  TotalParticipantUserModel({
    required this.nickname,
    required this.profileImageUrl,
    required this.totalParticipationCount,
  });
}

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
