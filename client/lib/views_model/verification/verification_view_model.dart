import 'package:get/get.dart';
import 'package:greenap/domain/models/challenge_item.dart';
import 'package:greenap/domain/models/dummy/challenge_dummy.dart';
import 'package:greenap/domain/models/dummy/my_challenge_dummy.dart';
import 'package:greenap/domain/models/my_challenge.dart';

class VerificationViewModel extends GetxController {
  final List<ChallengeItemModel> allChallengeItems =
      dummyChallengeCategory.expand((category) => category.challenges).toList();
  final RxList<MyChallengeModel> myChallenges = <MyChallengeModel>[].obs;

  @override
  void onInit() {
    super.onInit();
    fetchMyChallenges(); // 앱 시작 시 불러오기
  }

  void fetchMyChallenges() {
    // 실제 API 연동 시 해당 부분을 교체
    myChallenges.value = dummyMyChallenges;
  }

  ChallengeItemModel? findChallengeItemById(int challengeId) {
    return allChallengeItems.firstWhereOrNull((item) => item.id == challengeId);
  }
}
