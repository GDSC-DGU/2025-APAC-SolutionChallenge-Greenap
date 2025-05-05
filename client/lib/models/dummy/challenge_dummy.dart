import 'package:greenap/models/challenge.dart';
import 'package:greenap/enums/challenge.dart';

final List<ChallengeModel> dummyChallenges = [
  ChallengeModel(
    id: 1,
    title: '텀블러 사용하기',
    category: '자원절약',
    total_days: 30,
    elapsed_days: 10,
    progress: 33,
    ice_count: 2,
    is_cerficated_in_today: ChallengeCertificated.SUCESSED,
    status: ChallengeStatus.RUNNING,
    certification_data_list: [
      {'date': '2025-04-01', 'image': 'assets/images/cert1.png'},
      {'date': '2025-04-02', 'image': 'assets/images/cert2.png'},
    ],
  ),
  ChallengeModel(
    id: 2,
    title: '채식 식단 실천하기',
    category: '친환경',
    total_days: 14,
    elapsed_days: 14,
    progress: 100,
    ice_count: 4,
    is_cerficated_in_today: ChallengeCertificated.FAILED,
    status: ChallengeStatus.COMPLETED,
    certification_data_list: [
      {'date': '2025-03-28', 'image': 'assets/images/veg1.png'},
      {'date': '2025-03-29', 'image': 'assets/images/veg2.png'},
    ],
  ),
  ChallengeModel(
    id: 3,
    title: '대중교통 이용하기',
    category: '교통절감',
    total_days: 7,
    elapsed_days: 2,
    progress: 28,
    ice_count: 1,
    is_cerficated_in_today: ChallengeCertificated.FAILED,
    status: ChallengeStatus.RUNNING,
    certification_data_list: [
      {'date': '2025-04-01', 'image': 'assets/images/transport1.png'},
    ],
  ),
];
