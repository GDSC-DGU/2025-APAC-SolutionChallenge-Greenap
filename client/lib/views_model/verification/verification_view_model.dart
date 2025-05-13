import 'package:get/get.dart';
import 'package:greenap/domain/models/challenge_item.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';
import 'package:greenap/data/provider/challenge/my_challenge_provider.dart';
import 'package:collection/collection.dart';

class VerificationViewModel extends GetxController {
  final RxList<MyChallengeModel> myChallenges = <MyChallengeModel>[].obs;
  final List<ChallengeItemModel> allChallengeItems = [];

  late final MyChallengeProvider _provider;
  late final ChallengeViewModel _challengeViewModel;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<MyChallengeProvider>();
    _challengeViewModel = Get.find<ChallengeViewModel>();
    fetchMyChallenges();
  }

  Future<void> fetchMyChallenges() async {
    try {
      final response = await _provider.getMyChallenges();
      if (response.data != null) {
        myChallenges.value =
            response.data!.map((dto) {
              final matched = allChallengeItems.firstWhereOrNull(
                (item) => item.id == dto.challengeId,
              );
              return dto.toModel(mainImageUrl: matched?.mainImageUrl);
            }).toList();
      } else {
        print('[ERROR] 내 챌린지 목록 로딩 실패: ${response.message}');
      }
    } catch (e) {
      print('[EXCEPTION] 내 챌린지 조회 실패: $e');
    }
  }

  ChallengeItemModel? findChallengeItemById(int challengeId) {
    return _challengeViewModel.challengeList
        .expand((category) => category.challenges)
        .firstWhereOrNull((item) => item.id == challengeId);
  }
}
