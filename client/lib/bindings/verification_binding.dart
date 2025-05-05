import 'package:get/get.dart';
import 'package:greenap/views_model/verification/verification_view_model.dart';

class VerificationBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<VerificationViewModel>(() => VerificationViewModel());
  }
}
