import 'package:get/get.dart';
import 'package:greenap/views_model/mypage_view_model.dart';

class MypageBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<MypageViewModel>(() => MypageViewModel());
  }
}
