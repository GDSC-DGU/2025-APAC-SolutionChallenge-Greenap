import 'package:get/get.dart';
import 'package:greenap/views/home/home_screen.dart';
import 'package:greenap/views/feed/feed_screen.dart';
import 'package:greenap/views/challenge/challenge_screen.dart';
import 'package:greenap/views/mypage/mypage_screen.dart';
import 'package:greenap/views/root/root_screen.dart';
import 'package:greenap/views/verification/verification_screen.dart';

import 'package:greenap/bindings/home_binding.dart' as home;
import 'package:greenap/bindings/feed_binding.dart' as feed;
import 'package:greenap/bindings/mypage_binding.dart' as mypage;
import 'package:greenap/bindings/verification_binding.dart' as verification;
import 'app_routes.dart';

abstract class AppPages {
  static final List<GetPage> data = [
    GetPage(name: AppRoutes.ROOT, page: () => RootScreen()),
    GetPage(
      name: AppRoutes.HOME,
      page: () => HomeScreen(),
      binding: home.HomeBinding(),
    ),
    GetPage(name: AppRoutes.CHALLENGE, page: () => ChallengeScreen()),
    GetPage(
      name: AppRoutes.MYPAGE,
      page: () => MypageScreen(),
      binding: mypage.MypageBinding(),
    ),
    GetPage(
      name: AppRoutes.FEED,
      page: () => FeedScreen(),
      binding: feed.FeedBinding(),
    ),
    GetPage(
      name: AppRoutes.VERIFICATION,
      page: () => VerificationScreen(),
      binding: verification.VerificationBinding(),
    ),
  ];
}
