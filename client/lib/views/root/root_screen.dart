import 'package:flutter/material.dart';
import 'package:greenap/views/challenge/challenge_screen.dart';
import 'package:greenap/views/feed/feed_screen.dart';
import 'package:greenap/views/home/home_screen.dart';
import 'package:greenap/views/mypage/mypage_screen.dart';
import 'package:greenap/views/verification/verification_screen.dart';
import 'package:greenap/widgets/app_bar/custom_bottom_navigation_bar.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/views_model/root/root_view_model.dart';
import 'package:get/get.dart';

class RootScreen extends BaseScreen<RootViewModel> {
  RootScreen({super.key});

  final List<Widget> screens = [
    ChallengeScreen(),
    FeedScreen(),
    HomeScreen(),
    VerificationScreen(),
    MypageScreen(),
  ];

  @override
  Widget buildBody(BuildContext context) {
    return Obx(() => screens[viewModel.selectedIndex]);
  }

  @override
  Widget? buildBottomNavigationBar(BuildContext context) {
    return Obx(
      () => CustomBottomNavigationBar(
        currentIndex: viewModel.selectedIndex,
        onTap: viewModel.changeIndex,
      ),
    );
  }
}
