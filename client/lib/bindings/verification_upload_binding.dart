import 'package:get/get.dart';
import 'package:greenap/views_model/verification_upload/verification_upload_view_model.dart';

class VerificationUploadBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => VerificationUploadViewModel());
  }
}
