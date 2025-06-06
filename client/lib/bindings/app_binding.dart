import 'package:get/get.dart';
import 'package:greenap/data/provider/challenge/challenge_provider.dart';
import 'package:greenap/data/provider/auth/auth_provider.dart';
import 'package:greenap/data/provider/challenge/my_challenge_provider.dart';
import 'package:greenap/data/provider/category_detail/category_detail_provider.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';
import 'package:greenap/data/provider/verification/verification_provider.dart';
import 'package:greenap/data/provider/notification/encourage_provider.dart';
import 'package:greenap/data/provider/rank/rank_provider.dart';
import 'package:greenap/data/provider/report/report_provider.dart';

class AppBinding extends Bindings {
  @override
  void dependencies() {
    // Provider
    Get.lazyPut<AuthProvider>(() => AuthProvider(), fenix: true);
    Get.lazyPut<ChallengeProvider>(() => ChallengeProvider(), fenix: true);
    Get.lazyPut<MyChallengeProvider>(() => MyChallengeProvider(), fenix: true);
    Get.lazyPut<CategoryDetailProvider>(
      () => CategoryDetailProvider(),
      fenix: true,
    );
    Get.lazyPut<FeedProvider>(() => FeedProvider(), fenix: true);
    Get.lazyPut<VerificationProvider>(
      () => VerificationProvider(),
      fenix: true,
    );
    Get.lazyPut<EncourageProvider>(() => EncourageProvider(), fenix: true);
    Get.lazyPut<RankProvider>(() => RankProvider(), fenix: true);
    Get.lazyPut<ReportProvider>(() => ReportProvider(), fenix: true);
  }
}
