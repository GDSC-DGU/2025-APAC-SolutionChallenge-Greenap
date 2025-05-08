import '../challenge_report.dart';

final dummyChallengeReport = ChallengeReportModel(
  userChallengeId: 13,
  totalDays: 14,
  status: 'COMPLETED',
  successDays: 11,
  reportMessage: 'AI 생성 메시지',
  maxConsecutiveDays: 10,
  ranking: 1,
  certificationDataList: [
    {'date': '2025-03-28', 'is_certificated': 'true'},
    {'date': '2025-03-29', 'is_certificated': 'false'},
    {'date': '2025-03-30', 'is_certificated': 'false'},
    {'date': '2025-03-31', 'is_certificated': 'false'},
    {'date': '2025-04-01', 'is_certificated': 'ice'},
    {'date': '2025-04-02', 'is_certificated': 'ice'},
    {'date': '2025-04-03', 'is_certificated': 'true'},
    {'date': '2025-04-04', 'is_certificated': 'ice'},
    {'date': '2025-04-05', 'is_certificated': 'ice'},
    {'date': '2025-04-06', 'is_certificated': 'true'},
    {'date': '2025-04-07', 'is_certificated': 'true'},
    {'date': '2025-04-08', 'is_certificated': 'true'},
    {'date': '2025-04-09', 'is_certificated': 'true'},
    {'date': '2025-04-10', 'is_certificated': 'true'},
  ],
);
