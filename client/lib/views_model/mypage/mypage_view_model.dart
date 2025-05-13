import 'package:get/get.dart';
import 'package:greenap/domain/models/user.dart';
import 'package:greenap/data/provider/rank/rank_provider.dart';
import 'package:greenap/domain/models/all_ranking.dart';

class MypageViewModel extends GetxController {
  final Rxn<UserModel> user = Rxn<UserModel>();
  final Rxn<ParticipantModel> myRanking = Rxn<ParticipantModel>();
  late final RankProvider _provider;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<RankProvider>();
    getUserRank();
  }

  Future<void> getUserRank() async {
    final response = await _provider.getUserRanking();

    if (response.data != null) {
      myRanking.value = response.data!;
    } else {
      print('[ERROR] 랭킹 정보 로딩 실패: ${response.message}');
    }
  }
}
