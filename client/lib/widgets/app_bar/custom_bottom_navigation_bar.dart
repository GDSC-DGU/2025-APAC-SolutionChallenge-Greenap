import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_svg/svg.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/app_routes.dart';

class CustomBottomNavigationBar extends StatelessWidget {
  final int currentIndex;
  final Function(int) onTap;

  const CustomBottomNavigationBar({
    Key? key,
    required this.currentIndex,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final selectedColor = ColorSystem.mint!;
    final unselectedColor = ColorSystem.gray[500]!;

    return BottomNavigationBar(
      backgroundColor: ColorSystem.white,
      elevation: 0,
      type: BottomNavigationBarType.fixed,
      currentIndex: currentIndex,
      onTap: onTap,
      selectedItemColor: selectedColor,
      unselectedItemColor: unselectedColor,
      showUnselectedLabels: true,

      items: [
        BottomNavigationBarItem(
          icon: SvgPicture.asset(
            'assets/icons/challenge.svg',
            colorFilter: ColorFilter.mode(
              currentIndex == 0 ? selectedColor : unselectedColor,
              BlendMode.srcIn,
            ),
            width: 24,
            height: 24,
          ),
          label: "챌린지",
        ),
        BottomNavigationBarItem(
          icon: SvgPicture.asset(
            'assets/icons/feed.svg',
            colorFilter: ColorFilter.mode(
              currentIndex == 1 ? selectedColor : unselectedColor,
              BlendMode.srcIn,
            ),
            width: 24,
            height: 24,
          ),
          label: "피드",
        ),
        BottomNavigationBarItem(
          icon: SvgPicture.asset(
            'assets/icons/home.svg',
            colorFilter: ColorFilter.mode(
              currentIndex == 2 ? selectedColor : unselectedColor,
              BlendMode.srcIn,
            ),
            width: 24,
            height: 24,
          ),
          label: "홈",
        ),
        BottomNavigationBarItem(
          icon: SvgPicture.asset(
            'assets/icons/verification.svg',
            colorFilter: ColorFilter.mode(
              currentIndex == 3 ? selectedColor : unselectedColor,
              BlendMode.srcIn,
            ),
            width: 24,
            height: 24,
          ),
          label: "인증",
        ),
        BottomNavigationBarItem(
          icon: SvgPicture.asset(
            'assets/icons/mypage.svg',
            colorFilter: ColorFilter.mode(
              currentIndex == 4 ? selectedColor : unselectedColor,
              BlendMode.srcIn,
            ),
            width: 24,
            height: 24,
          ),
          label: "마이페이지",
        ),
      ],
    );
  }
}
