import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

class CustomBottomNavigationBar extends StatelessWidget {
  final int currentIndex;
  final Function(int) onTap;

  const CustomBottomNavigationBar({
    super.key,
    required this.currentIndex,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final selectedColor = ColorSystem.mint;
    final unselectedColor = ColorSystem.gray[500]!;

    return BottomNavigationBar(
      backgroundColor: ColorSystem.white,
      currentIndex: currentIndex,
      onTap: onTap,
      type: BottomNavigationBarType.fixed,
      selectedItemColor: selectedColor,
      unselectedItemColor: unselectedColor,
      selectedLabelStyle: FontSystem.NavigationLabel,
      unselectedLabelStyle: FontSystem.NavigationLabel,
      items: [
        _buildBarItem("챌린지", "assets/icons/challenge.svg", 0),
        _buildBarItem("피드", "assets/icons/feed.svg", 1),
        _buildBarItem("홈", "assets/icons/home.svg", 2),
        _buildBarItem("인증", "assets/icons/verification.svg", 3),
        _buildBarItem("마이페이지", "assets/icons/mypage.svg", 4),
      ],
    );
  }

  BottomNavigationBarItem _buildBarItem(
    String label,
    String iconPath,
    int index,
  ) {
    return BottomNavigationBarItem(
      icon: SvgPicture.asset(
        iconPath,
        colorFilter: ColorFilter.mode(
          currentIndex == index ? ColorSystem.mint! : ColorSystem.gray[500]!,
          BlendMode.srcIn,
        ),
        width: 24,
        height: 24,
      ),
      label: label,
    );
  }
}
