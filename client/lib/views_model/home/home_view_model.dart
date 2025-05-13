import 'package:get/get.dart';
import 'package:greenap/data/provider/challenge/challenge_provider.dart';
import 'package:greenap/domain/models/challenge_category.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/data/provider/challenge/my_challenge_provider.dart';

class HomeViewModel extends GetxController {
  final RxList<ChallengeCategoryModel> challengeCategories =
      <ChallengeCategoryModel>[].obs;
  late final MyChallengeProvider _myChallengeProvider;
  late final ChallengeProvider _challengeProvider;

  @override
  void onInit() {
    super.onInit();
    _myChallengeProvider = Get.find<MyChallengeProvider>();
    _challengeProvider = Get.find<ChallengeProvider>();
    fetchChallengeCategories();
  }

  Future<void> fetchChallengeCategories() async {
    // 1. 데이터 조회
    final categoryResponse = await _challengeProvider.getChallengeCategories();
    final myChallengeResponse = await _myChallengeProvider.getMyChallenges();

    if (categoryResponse.data == null || myChallengeResponse.data == null) {
      print('[ERROR] 데이터 조회 실패');
      return;
    }

    final categories = categoryResponse.data!;
    final myChallenges = myChallengeResponse.data!;

    // 2. 카테고리별 참여 챌린지 수 집계
    final Map<String, int> categoryCountMap = {};

    for (final challenge in myChallenges) {
      final categoryName = challenge.category;
      categoryCountMap[categoryName] =
          (categoryCountMap[categoryName] ?? 0) + 1;
    }

    categories.sort((a, b) {
      final countA = categoryCountMap[a.title] ?? 0;
      final countB = categoryCountMap[b.title] ?? 0;
      return countB.compareTo(countA);
    });

    // 4. 저장
    challengeCategories.assignAll(categories);
  }
}
