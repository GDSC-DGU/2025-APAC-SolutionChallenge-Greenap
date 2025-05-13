import 'package:get/get.dart';
import 'package:greenap/domain/models/user.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:greenap/data/provider/rank/rank_provider.dart';

class MypageViewModel extends GetxController {
  final Rxn<UserModel> user = Rxn<UserModel>();
  final Rxn<int> myRanking = Rxn<int>();
  final Rxn<int> longestConsecutiveParticipationCount = Rxn<int>();
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();
  late final RankProvider _provider;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<RankProvider>();
    getUserRank();
    _loadUserInfoFromStorage();
  }

  Future<void> getUserRank() async {
    final response = await _provider.getUserRanking();

    if (response.data != null) {
      myRanking.value = response.data!.rank;
      longestConsecutiveParticipationCount.value =
          response.data!.longestConsecutiveParticipationCount;
    } else {
      print('[ERROR] 랭킹 정보 로딩 실패: ${response.message}');
    }
  }

  Future<void> _loadUserInfoFromStorage() async {
    try {
      final nickname = await _secureStorage.read(key: 'nickname');
      final profileImageUrl = await _secureStorage.read(key: 'profileImageUrl');

      if (nickname != null && profileImageUrl != null) {
        user.value = UserModel(
          nickname: nickname,
          profileImageUrl: profileImageUrl,
        );
      } else {
        print('[WARN] 저장된 사용자 정보가 없습니다.');
      }
    } catch (e) {
      print('[ERROR] 사용자 정보 불러오기 실패: $e');
    }
  }
}
