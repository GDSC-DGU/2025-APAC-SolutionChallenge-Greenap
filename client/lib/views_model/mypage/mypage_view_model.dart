import 'package:get/get.dart';
import 'package:greenap/domain/models/user.dart';

class MypageViewModel extends GetxController {
  final Rxn<UserModel> user = Rxn<UserModel>();
  @override
  void onInit() {
    super.onInit();

    // 로그인 후 저장된 사용자 정보 로드 (실제 로직은 로그인 후 저장)
    user.value = UserModel(
      nickname: '이정선',
      profileImageUrl:
          'https://www.studiopeople.kr/common/img/default_profile.png',
    );
  }
}
