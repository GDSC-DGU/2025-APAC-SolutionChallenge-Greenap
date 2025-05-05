import 'package:flutter/material.dart';
import 'package:greenap/views/challenge/challenge_screen.dart';
import 'package:greenap/views/feed/feed_screen.dart';
import 'package:greenap/views/home/home_screen.dart';
import 'package:greenap/views/mypage/mypage_screen.dart';
import 'package:greenap/views/verification/verification_screen.dart';
import 'package:greenap/widgets/app_bar/custom_bottom_navigation_bar.dart';

class RootScreen extends StatefulWidget {
  const RootScreen({super.key});

  @override
  State<RootScreen> createState() => _RootScreenState();
}

class _RootScreenState extends State<RootScreen> {
  int _selectedIndex = 2; // 기본 홈

  List<Widget> get _pages => [
    ChallengeScreen(),
    FeedScreen(),
    HomeScreen(),
    VerificationScreen(),
    MypageScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: IndexedStack(index: _selectedIndex, children: _pages),
        bottomNavigationBar: CustomBottomNavigationBar(
          currentIndex: _selectedIndex,
          onTap: (index) {
            setState(() {
              _selectedIndex = index;
            });
          },
        ),
      ),
    );
  }
}
