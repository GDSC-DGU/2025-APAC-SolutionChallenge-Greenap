import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:get/get.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/views_model/mypage/mypage_view_model.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/widgets/app_bar/default_app_bar.dart';
import './widgets/mypage_menu_item.dart';

class MypageScreen extends BaseScreen<MypageViewModel> {
  const MypageScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return DefaultAppBar(title: "마이페이지");
  }

  @override
  Widget buildBody(BuildContext context) {
    return Obx(() {
      final user = viewModel.user.value;

      if (user == null) return Center(child: CircularProgressIndicator());

      return SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            // 프로필 이미지
            CircleAvatar(
              radius: 40,
              backgroundImage: NetworkImage(user.profileImageUrl),
              backgroundColor: ColorSystem.gray[100],
            ),
            const SizedBox(height: 12),
            Text(
              user.nickname,
              style: FontSystem.Head2.copyWith(color: ColorSystem.gray[800]),
            ),
            const SizedBox(height: 32),

            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: ColorSystem.white,
                borderRadius: BorderRadius.circular(8),
                boxShadow: [
                  BoxShadow(
                    color: ColorSystem.black.withOpacity(0.04),
                    spreadRadius: 2,
                    blurRadius: 4,
                    offset: Offset(0, 0),
                  ),
                ],
              ),
              child: Column(
                children: [
                  Row(
                    children: [
                      Text(
                        "랭킹",
                        style: FontSystem.Head2.copyWith(
                          color: ColorSystem.gray[700],
                        ),
                      ),
                      Spacer(),
                      Text(
                        "전체보기",
                        style: FontSystem.Body2.copyWith(
                          color: ColorSystem.gray[600],
                        ),
                      ),
                      SizedBox(width: 4),
                      SvgPicture.asset(
                        "assets/icons/arrow.svg",
                        width: 16,
                        height: 16,
                      ),
                    ],
                  ),
                  SizedBox(height: 20),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _buildRankingItem('챌린지 랭킹', '10'),
                      SizedBox(width: 16),
                      _buildRankingItem('최장 연속 일수', '10'),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            MypageMenuItem(
              icon: SvgPicture.asset(
                'assets/icons/mypage.svg',
                color: ColorSystem.gray[700],
                width: 16,
                height: 16,
              ),
              label: '프로필 관리',
              onTap: () {
                Get.toNamed('/profile-edit');
              },
            ),
            MypageMenuItem(
              icon: SvgPicture.asset(
                'assets/icons/bell.svg',
                width: 16,
                height: 16,
              ),
              label: '알림 설정',
              onTap: () {
                Get.toNamed('/notification-setting');
              },
            ),
            MypageMenuItem(
              icon: SvgPicture.asset(
                'assets/icons/feed.svg',
                color: ColorSystem.gray[700],
                width: 16,
                height: 16,
              ),
              label: '내 피드',
              onTap: () {
                Get.toNamed('/my-feed-category');
              },
            ),
          ],
        ),
      );
    });
  }

  Widget _buildRankingItem(String title, String value) {
    return Expanded(
      child: Container(
        padding: EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: ColorSystem.white,
          borderRadius: BorderRadius.circular(4),
          boxShadow: [
            BoxShadow(
              color: ColorSystem.black.withOpacity(0.04),
              spreadRadius: 2,
              blurRadius: 4,
              offset: Offset(0, 0),
            ),
          ],
        ),
        child: Column(
          children: [
            Text(
              title,
              style: FontSystem.Body2Bold.copyWith(color: ColorSystem.mint),
            ),
            const SizedBox(height: 4),
            Text(
              value,
              style: FontSystem.Body2Bold.copyWith(color: ColorSystem.mint),
            ),
          ],
        ),
      ),
    );
  }
}
