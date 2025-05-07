import 'package:get/get.dart';
import 'package:greenap/views/home/home_screen.dart';
import 'package:greenap/views/feed/feed_screen.dart';
import 'package:greenap/views/challenge/challenge_screen.dart';
import 'package:greenap/views/mypage/mypage_screen.dart';
import 'package:greenap/views/root/root_screen.dart';
import 'package:greenap/views/verification/verification_screen.dart';
import 'package:greenap/views/category_detail/category_detail_screen.dart';
import 'package:greenap/models/challenge_category.dart';
import 'package:greenap/bindings/root_binding.dart';
import 'package:greenap/bindings/category_detail_binding.dart';
import 'app_routes.dart';

abstract class AppPages {
  static final List<GetPage> data = [
    GetPage(
      name: AppRoutes.ROOT,
      page: () => RootScreen(),
      binding: RootBinding(),
    ),
    GetPage(name: AppRoutes.HOME, page: () => HomeScreen()),
    GetPage(name: AppRoutes.CHALLENGE, page: () => ChallengeScreen()),
    GetPage(
      name: AppRoutes.CATEGORY,
      page: () => CategoryDetailScreen(),
      binding: CategoryDetailBinding(),
    ),
    GetPage(name: AppRoutes.MYPAGE, page: () => MypageScreen()),
    GetPage(name: AppRoutes.FEED, page: () => FeedScreen()),
    GetPage(name: AppRoutes.VERIFICATION, page: () => VerificationScreen()),
  ];
}
