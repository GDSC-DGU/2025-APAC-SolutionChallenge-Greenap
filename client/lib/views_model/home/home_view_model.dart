import 'package:get/get.dart';
import 'package:greenap/data/provider/challenge/challenge_provider.dart';
import 'package:greenap/domain/models/challenge_category.dart';
import 'package:greenap/data/provider/challenge/my_challenge_provider.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:greenap/data/provider/notification/encourage_provider.dart';
import 'package:greenap/domain/models/my_challenge.dart';

class HomeViewModel extends GetxController {
  final RxList<ChallengeCategoryModel> challengeCategories =
      <ChallengeCategoryModel>[].obs;
  late final MyChallengeProvider _myChallengeProvider;
  late final ChallengeProvider _challengeProvider;
  late final EncourageProvider _encourageProvider;
  final Rxn<String> nickname = Rxn<String>();
  final Rxn<String> encourageMessage = Rxn<String>();
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();
  final RxList<MyChallengeModel> myChallengeList = <MyChallengeModel>[].obs;

  @override
  void onInit() {
    super.onInit();
    _myChallengeProvider = Get.find<MyChallengeProvider>();
    _challengeProvider = Get.find<ChallengeProvider>();
    _encourageProvider = Get.find<EncourageProvider>();
    fetchEncourageMessage();
    fetchChallengeCategories();
    userInfo();
  }

  Future<void> fetchEncourageMessage() async {
    final response = await _encourageProvider.getEncourageMessage();
    if (response.message != null) {
      encourageMessage.value = response.data;
      print('[DEBUG] 메시지 : ${response.data}');
    } else {
      print('[DEBUG] 메시지 없음');
    }
  }

  Future<void> userInfo() async {
    final storedNickname = await _secureStorage.read(key: 'nickname');
    if (storedNickname != null) {
      nickname.value = storedNickname;
      print('[DEBUG] 닉네임 불러오기 성공: $storedNickname');
    } else {
      print('[DEBUG] 저장된 닉네임 없음');
    }
  }

  Future<void> fetchChallengeCategories() async {
    final categoryResponse = await _challengeProvider.getChallengeCategories();
    final myChallengeResponse = await _myChallengeProvider.getMyChallenges();

    if (categoryResponse.data == null || myChallengeResponse.data == null) {
      print('[ERROR] 데이터 조회 실패');
      return;
    }

    final categories = categoryResponse.data!;
    final myChallenges = myChallengeResponse.data!;

    myChallengeList.assignAll(myChallenges.map((dto) => dto.toModel()));

    final Map<String, int> categoryCountMap = {};
    for (final challenge in myChallenges) {
      categoryCountMap[challenge.category] =
          (categoryCountMap[challenge.category] ?? 0) + 1;
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
