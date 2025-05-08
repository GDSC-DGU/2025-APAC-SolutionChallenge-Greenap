import 'package:get/get.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/domain/models/dummy/challenge_detail_dummy.dart';

class VerificationUploadViewModel extends GetxController {
  late final int challengeId;
  late final ChallengeDetailModel challengeDetail;
  final RxBool isChecked = false.obs;

  @override
  void onInit() {
    super.onInit();
    challengeId = Get.arguments as int;
    fetchChallengeDetail();
  }

  void fetchChallengeDetail() {
    // 실제 호출로 대체 필요
    try {
      final detail = dummyChallengeDetails.firstWhere(
        (detail) => detail.id == challengeId,
        orElse: () => throw Exception('챌린지 상세 정보가 없습니다.'),
      );
      challengeDetail = detail;
    } catch (e) {
      print('챌린지 상세 불러오기 실패: $e');
    }
  }
}
