class EncourageDto {
  final String message;
  final String challengeTitle;
  final int progress;
  final int userChallengeId;

  EncourageDto({
    required this.message,
    required this.challengeTitle,
    required this.progress,
    required this.userChallengeId,
  });

  factory EncourageDto.fromJson(Map<String, dynamic> json) {
    return EncourageDto(
      message: json['message'] ?? '',
      challengeTitle: json['challengeTitle'] ?? '',
      progress: json['progress'] ?? '',
      userChallengeId: json['userChallengeId'] ?? '',
    );
  }
}
