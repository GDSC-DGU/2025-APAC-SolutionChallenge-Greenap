import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/views/challenge/challenge_screen.dart' as challenge;
import 'package:greenap/views/feed/feed_screen.dart' as feed;
import 'package:greenap/views/home/home_screen.dart' as home;
import 'package:greenap/views/mypage/mypage_screen.dart' as mypage;
import 'package:greenap/views/verification/verification_screen.dart'
    as verification;
import 'package:greenap/widgets/app_bar/custom_bottom_navigation_bar.dart';

class RootScreen extends StatefulWidget {
  const RootScreen({super.key});

  @override
  State<RootScreen> createState() => _RootScreenState();
}

class _RootScreenState extends State<RootScreen> {
  int _selectedIndex = 2; // 기본값: 홈

  final List<Widget> _pages = [
    challenge.ChallengeScreen(),
    feed.FeedScreen(),
    home.HomeScreen(),
    verification.VerificationScreen(),
    mypage.MypageScreen(),
  ];

  void _onTap(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        backgroundColor: ColorSystem.white,
        body: IndexedStack(index: _selectedIndex, children: _pages),
        bottomNavigationBar: CustomBottomNavigationBar(
          currentIndex: _selectedIndex,
          onTap: _onTap,
        ),
      ),
    );
  }
}
