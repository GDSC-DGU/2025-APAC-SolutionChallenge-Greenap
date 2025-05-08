import 'package:get/get.dart';
import 'package:greenap/views_model/my_challenge_detail/my_challenge_detail_view_model.dart';

class MyChallengeDetailBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => MyChallengeDetailViewModel());
  }
}
