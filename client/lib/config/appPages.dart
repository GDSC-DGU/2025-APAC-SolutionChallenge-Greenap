import 'package:get/get.dart';
import 'package:greenap/views/home/home_screen.dart';
import 'package:greenap/views/root/root_screen.dart';
// import 'package:greenap/views/feed/feed_screen.dart';
// import 'package:greenap/views/challenge/challenge_screen.dart';
// import 'package:greenap/views/ranking/mypage_screen.dart';
// import 'package:greenap/views/verification/verification_screen.dart';

import 'package:greenap/bindings/home_binding.dart' as home;
import 'package:greenap/bindings/feed_binding.dart' as feed;
import 'package:greenap/bindings/challenge_binding.dart' as challenge;
import 'package:greenap/bindings/mypage_binding.dart' as mypage;
import 'package:greenap/bindings/verification_binding.dart' as verification;
import 'package:greenap/bindings/root_binding.dart' as root;
import 'appRoutes.dart';

abstract class AppPages {
  static final List<GetPage> data = [
    GetPage(
      name: AppRoutes.ROOT,
      page: () => const RootScreen(),
      binding: root.RootBinding(),
    ),
    GetPage(
      name: AppRoutes.HOME,
      page: () => const HomeScreen(),
      binding: home.HomeBinding(),
    ),
  ];
}
