import 'package:get/get.dart';
import 'package:greenap/data/provider/challenge/challenge_provider.dart';
import 'package:greenap/domain/models/challenge_category.dart';

class ChallengeViewModel extends GetxController {
  final challengeList = <ChallengeCategoryModel>[].obs;

  final isLeftSelected = true.obs;
  final isLoading = false.obs;

  final ChallengeProvider _provider = Get.find<ChallengeProvider>();
  @override
  void onInit() {
    super.onInit();
    fetchChallengeCategories();
  }

  void toggleView(bool leftSelected) {
    isLeftSelected.value = leftSelected;
  }

  Future<void> fetchChallengeCategories() async {
    if (isLoading.value) return;

    isLoading.value = true;
    try {
      final result = await _provider.getChallengeCategories();
      if (result.data != null) {
        challengeList.value = result.data!;
      }
    } catch (e) {
      print('[ERROR] 챌린지 불러오기 실패: $e');
    } finally {
      isLoading.value = false;
    }
  }
}
