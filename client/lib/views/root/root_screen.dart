import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/views/challenge/challenge_screen.dart' as challenge;
import 'package:greenap/views/feed/feed_screen.dart' as feed;
import 'package:greenap/views/home/home_screen.dart' as home;
import 'package:greenap/views/mypage/mypage_screen.dart' as mypage;
import 'package:greenap/views/verification/verification_screen.dart'
    as verification;
import 'package:greenap/widgets/app_bar/custom_bottom_navigation_bar.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/root_view_model.dart';

class RootScreen extends GetView<RootViewModel> {
  const RootScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final List<Widget> pages = [
      challenge.ChallengeScreen(),
      feed.FeedScreen(),
      home.HomeScreen(),
      verification.VerificationScreen(),
      mypage.MypageScreen(),
    ];

    return SafeArea(
      child: Scaffold(
        backgroundColor: ColorSystem.white,
        body: Obx(
          () => IndexedStack(
            index: controller.selectedIndex.value,
            children: pages,
          ),
        ),
        bottomNavigationBar: Obx(
          () => CustomBottomNavigationBar(
            currentIndex: controller.selectedIndex.value,
            onTap: controller.changeIndex,
          ),
        ),
      ),
    );
  }
}
