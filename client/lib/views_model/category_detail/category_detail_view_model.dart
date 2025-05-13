import 'package:get/get.dart';
import 'package:greenap/domain/models/challenge_category.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/data/provider/category_detail/category_detail_provider.dart';

class CategoryDetailViewModel extends GetxController {
  late final ChallengeCategoryModel category;
  late final CategoryDetailProvider _provider;
  final challengeDetail = Rxn<ChallengeDetailModel>();

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<CategoryDetailProvider>();
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

  Future<ResponseWrapper<int>> joinChallenge({
    required int challengeId,
    required int duration,
  }) async {
    final response = await _provider.joinChallenge(
      challengeId: challengeId,
      participantsDate: duration,
    );

    if (response.data != null) {
      return response;
    } else {
      print('[ERROR]: ${response.message}');
      return ResponseWrapper(
        code: response.code,
        data: null,
        message: response.message,
      );
    }
  }
}
