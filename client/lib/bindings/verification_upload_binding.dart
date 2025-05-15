import 'package:get/get.dart';
import 'package:greenap/views_model/verification_upload/verification_upload_view_model.dart';
import 'package:greenap/domain/models/my_challenge.dart';

class VerificationUploadBinding extends Bindings {
  @override
  void dependencies() {
    final args = Get.arguments as Map<String, dynamic>;

    Get.lazyPut<VerificationUploadViewModel>(
      () => VerificationUploadViewModel(
        challengeId: args['challengeId'] as int,
        userChallengeId: args['userChallengeId'] as int,
        myChallengeModel: args['myChallengeModel'] as MyChallengeModel,
      ),
    );
  }
}
