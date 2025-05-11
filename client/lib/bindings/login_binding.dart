import 'package:get/get.dart';
import 'package:greenap/views_model/login/login_view_model.dart';

class LoginBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => LoginViewModel());
  }
}
