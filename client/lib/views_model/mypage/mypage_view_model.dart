import 'package:get/get.dart';
import 'package:greenap/domain/models/user.dart';
import 'package:greenap/data/provider/rank/rank_provider.dart';
import 'package:greenap/domain/models/all_ranking.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class MypageViewModel extends GetxController {
  final Rxn<UserModel> user = Rxn<UserModel>();
  final Rxn<ParticipantModel> myRanking = Rxn<ParticipantModel>();
  late final RankProvider _provider;
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

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
      print('[WARN] 유저 랭킹 없음 또는 응답 데이터 null');
      final nickname = await _secureStorage.read(key: 'nickname');
      final profileImageUrl = await _secureStorage.read(key: 'profileImageUrl');
      if (nickname != null && profileImageUrl != null) {
        myRanking.value = ParticipantModel(
          rank: null,
          user: ParticipantUserModel(
            nickname: nickname,
            profileImageUrl: profileImageUrl,
            longestConsecutiveParticipationCount: null,
          ),
        );
      } else {
        print('[ERROR] SecureStorage에 사용자 정보가 없습니다.');
        myRanking.value = null;
      }
    }
  }
}
