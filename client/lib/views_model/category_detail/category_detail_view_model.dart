import 'package:get/get.dart';
import 'package:greenap/domain/models/challenge_category.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/data/provider/category_detail/category_detail_provider.dart';
import 'package:greenap/data/dto/challege_detail_dto.dart';

class CategoryDetailViewModel extends GetxController {
  late final ChallengeCategoryModel category;
  late final CategoryDetailProvider _provider =
      Get.find<CategoryDetailProvider>();
  final challengeDetail = Rxn<ChallengeDetailModel>();

  @override
  void onInit() {
    super.onInit();
    category = Get.arguments as ChallengeCategoryModel;
  }

  /// 챌린지 상세 정보 조회
  Future<void> fetchChallengeDetail(int challengeId) async {
    final response = await _provider.getChallengeDetail(challengeId);

    if (response.data != null) {
      challengeDetail.value = response.data;
    } else {
      print('[ERROR] 챌린지 상세 정보 로딩 실패: ${response.message}');
    }
  }

  /// 챌린지 참여
  Future<ResponseWrapper> joinChallenge({
    required int challengeId,
    required int duration,
  }) async {
    return await _provider.joinChallenge(
      challengeId: challengeId,
      participantsDate: duration,
    );
  }
}
