import 'package:get/get.dart';
import 'package:greenap/views_model/login/login_view_model.dart';
import 'package:greenap/data/provider/auth/auth_provider.dart';

class LoginBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<AuthProvider>(() => AuthProvider());
    Get.lazyPut(() => LoginViewModel());
  }
}
