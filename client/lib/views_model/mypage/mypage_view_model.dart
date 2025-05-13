import 'package:get/get.dart';
import 'package:greenap/domain/models/user.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class MypageViewModel extends GetxController {
  final Rxn<UserModel> user = Rxn<UserModel>();
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  @override
  void onInit() {
    super.onInit();
    _loadUserInfoFromStorage();
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
