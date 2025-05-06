import 'package:get/get.dart';
import 'package:greenap/views_model/root/root_view_model.dart';
import 'package:greenap/views_model/home/home_view_model.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';
import 'package:greenap/views_model/feed/feed_view_model.dart';
import 'package:greenap/views_model/verification/verification_view_model.dart';
import 'package:greenap/views_model/mypage/mypage_view_model.dart';

class RootBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<RootViewModel>(() => RootViewModel());
    Get.lazyPut<ChallengeViewModel>(() => ChallengeViewModel());
    Get.lazyPut<FeedViewModel>(() => FeedViewModel());
    Get.lazyPut<HomeViewModel>(() => HomeViewModel());
    Get.lazyPut<MypageViewModel>(() => MypageViewModel());
    Get.lazyPut<VerificationViewModel>(() => VerificationViewModel());
  }
}
