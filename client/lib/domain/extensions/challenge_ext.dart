import '../enums/challenge.dart';

extension ChallengeDurationExtension on ChallengeDuration {
  int get days {
    switch (this) {
      case ChallengeDuration.sevenDays:
        return 7;
      case ChallengeDuration.tenDays:
        return 10;
      case ChallengeDuration.fifteenDays:
        return 15;
      case ChallengeDuration.thirtyDays:
        return 30;
    }
  }
}

extension ChallengeFilterExtension on ChallengeFilterStatus {
  ChallengeStatus? toChallengeStatus() {
    switch (this) {
      case ChallengeFilterStatus.running:
        return ChallengeStatus.running;
      case ChallengeFilterStatus.completed:
        return ChallengeStatus.completed;
      case ChallengeFilterStatus.all:
        return null;
    }
  }
}
