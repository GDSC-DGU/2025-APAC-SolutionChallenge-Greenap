import 'package:get/get.dart';
import 'package:greenap/views_model/challenge_view_model.dart';

class HomeBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<ChallengeViewModel>(() => ChallengeViewModel());
  }
}
