import 'package:get/get.dart';
import 'package:greenap/views/home/home_screen.dart';
import 'package:greenap/views/feed/feed_screen.dart';
import 'package:greenap/views/challenge/challenge_screen.dart';
import 'package:greenap/views/mypage/mypage_screen.dart';
import 'package:greenap/views/root/root_screen.dart';
import 'package:greenap/views/verification/verification_screen.dart';
import 'package:greenap/views/category_detail/category_detail_screen.dart';
import 'package:greenap/views/my_challenge_detail/my_challenge_detail_screen.dart';
import 'package:greenap/views/verification_upload/verification_upload_screen.dart';
import 'package:greenap/bindings/my_challenge_detail_binding.dart';
import 'package:greenap/bindings/root_binding.dart';
import 'package:greenap/bindings/category_detail_binding.dart';
import 'package:greenap/bindings/verification_upload_binding.dart';
import 'package:greenap/views/my_feed_category/my_feed_category_screen.dart';
import 'package:greenap/views/my_feed_list/my_feed_list_screen.dart';
import 'package:greenap/bindings/my_feed_list_binding.dart';
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
    GetPage(
      name: AppRoutes.MYCHALLENGE,
      page: () => MyChallengeDetailScreen(),
      binding: MyChallengeDetailBinding(),
    ),
    GetPage(name: AppRoutes.MYPAGE, page: () => MypageScreen()),
    GetPage(name: AppRoutes.MYPAGE, page: () => MypageScreen()),
    GetPage(name: AppRoutes.FEED, page: () => FeedScreen()),
    GetPage(name: AppRoutes.MYFEEDCATEGORY, page: () => MyFeedCategoryScreen()),
    GetPage(
      name: AppRoutes.MYFEEDLIST,
      page: () => MyFeedListScreen(),
      binding: MyFeedListBinding(),
    ),
    GetPage(
      name: AppRoutes.VERIFICATIONUPLOAD,
      page: () => VerificationUploadScreen(),
      binding: VerificationUploadBinding(),
    ),
  ];
}
