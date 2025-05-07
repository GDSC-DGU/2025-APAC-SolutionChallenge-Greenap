import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';

final List<MyChallengeModel> dummyMyChallenges = [
  MyChallengeModel(
    id: 1,
    challengeId: 8,
    title: '텀블러 사용하기',
    category: '자원절약',
    totalDays: 30,
    elapsedDays: 10,
    progress: 33,
    iceCount: 2,
    isCerficatedInToday: ChallengeCertificated.SUCESSED,
    status: ChallengeStatus.running,
    certificationDataList: [
      {'date': '2025-04-01', 'image': 'assets/images/cert1.png'},
      {'date': '2025-04-02', 'image': 'assets/images/cert2.png'},
    ],
  ),
  MyChallengeModel(
    id: 2,
    challengeId: 13,
    title: '채식 식단 실천하기',
    category: '친환경',
    totalDays: 14,
    elapsedDays: 14,
    progress: 100,
    iceCount: 4,
    isCerficatedInToday: ChallengeCertificated.FAILED,
    status: ChallengeStatus.completed,
    certificationDataList: [
      {'date': '2025-03-28', 'image': 'assets/images/veg1.png'},
      {'date': '2025-03-29', 'image': 'assets/images/veg2.png'},
    ],
  ),
  MyChallengeModel(
    id: 3,
    challengeId: 18,
    title: '대중교통 이용하기',
    category: '교통절감',
    totalDays: 7,
    elapsedDays: 2,
    progress: 28,
    iceCount: 1,
    isCerficatedInToday: ChallengeCertificated.FAILED,
    status: ChallengeStatus.running,
    certificationDataList: [
      {'date': '2025-04-01', 'image': 'assets/images/transport1.png'},
    ],
  ),
];
