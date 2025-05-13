// ranking_model.dart

class RankingModel {
  final int totalParticipantsCount;
  final List<ParticipantModel> top100Participants;

  RankingModel({
    required this.totalParticipantsCount,
    required this.top100Participants,
  });
}

class ParticipantModel {
  final int rank;
  final ParticipantUserModel user;

  ParticipantModel({required this.rank, required this.user});
}

class ParticipantUserModel {
  final String nickname;
  final String profileImageUrl;
  final int longestConsecutiveParticipationCount;

  ParticipantUserModel({
    required this.nickname,
    required this.profileImageUrl,
    required this.longestConsecutiveParticipationCount,
  });
}
