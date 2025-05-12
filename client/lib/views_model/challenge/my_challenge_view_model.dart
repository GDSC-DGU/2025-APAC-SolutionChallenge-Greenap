import 'package:get/get.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/data/provider/challenge/my_challenge_provider.dart';
import 'package:greenap/domain/models/challenge_category.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';

class MyChallengeViewModel extends GetxController {
  final status = ChallengeFilterStatus.all.obs;
  final myChallenges = <MyChallengeModel>[].obs;
  final isLoading = false.obs;

  final MyChallengeProvider _provider = Get.find<MyChallengeProvider>();
  final ChallengeViewModel _challengeViewModel = Get.find<ChallengeViewModel>();

  @override
  void onInit() {
    super.onInit();
    fetchMyChallenges();
  }

  void setStatus(ChallengeFilterStatus value) {
    status.value = value;
  }

  Future<void> fetchMyChallenges() async {
    isLoading.value = true;

    final response = await _provider.getMyChallenges();
    if (response.data != null) {
      final allChallenges =
          _challengeViewModel.challengeList.map((e) => e.toModel()).toList();

      myChallenges.value =
          response.data!.map((dto) {
            String? imageUrl;
            for (final category in allChallenges) {
              for (final challenge in category.challenges) {
                if (challenge.id == dto.challengeId) {
                  imageUrl = challenge.mainImageUrl;
                  break;
                }
              }
            }
            return dto.toModel(mainImageUrl: imageUrl);
          }).toList();
    } else {
      print('[ERROR] 마이 챌린지 가져오기 실패: ${response.message}');
    }

    isLoading.value = false;
  }
}
