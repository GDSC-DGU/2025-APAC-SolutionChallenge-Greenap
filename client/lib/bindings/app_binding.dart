import 'package:get/get.dart';
import 'package:greenap/data/provider/challenge/challenge_provider.dart';
import 'package:greenap/data/provider/auth/auth_provider.dart';
import 'package:greenap/data/provider/challenge/my_challenge_provider.dart';

class AppBinding extends Bindings {
  @override
  void dependencies() {
    // Provider
    Get.lazyPut<AuthProvider>(() => AuthProvider());
    Get.lazyPut<ChallengeProvider>(() => ChallengeProvider());
    Get.lazyPut<MyChallengeProvider>(() => MyChallengeProvider());
  }
}
