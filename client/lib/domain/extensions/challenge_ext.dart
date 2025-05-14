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
  Set<ChallengeStatus>? toChallengeStatus() {
    switch (this) {
      case ChallengeFilterStatus.running:
        return {ChallengeStatus.running};
      case ChallengeFilterStatus.completed:
        return {
          ChallengeStatus.completed,
          ChallengeStatus.waiting,
          ChallengeStatus.pending,
        };
      case ChallengeFilterStatus.all:
        return null;
    }
  }
}

extension ChallengeStatusMapper on String {
  ChallengeStatus toChallengeStatus() {
    switch (this) {
      case 'RUNNING':
        return ChallengeStatus.running;
      case 'PENDING':
        return ChallengeStatus.pending;
      case 'WAITING':
        return ChallengeStatus.waiting;
      case 'COMPLETED':
        return ChallengeStatus.completed;
      default:
        // 예외 상황을 명확히 처리하거나 디폴트
        return ChallengeStatus.running;
    }
  }
}
