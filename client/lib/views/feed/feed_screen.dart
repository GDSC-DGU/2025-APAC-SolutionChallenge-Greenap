import 'package:flutter/material.dart';
import 'package:greenap/views_model/feed/feed_view_model.dart';
import 'package:get/get.dart';
import 'package:greenap/widgets/app_bar/default_app_bar.dart';
import 'package:greenap/views/base/base_screen.dart';
import './widgets/feed_card.dart';
import 'widgets/feed_category_filter.dart';

class FeedScreen extends BaseScreen<FeedViewModel> {
  FeedScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return DefaultAppBar(title: '챌린지 피드');
  }

  final categories = ['전체', '자원 절약', '교통 절감', '재사용', '친환경'];

  @override
  Widget buildBody(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(height: 20),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: FeedCategoryFilter(categories: categories),
        ),
        const SizedBox(height: 20),

        // 🔽 이 부분만 스크롤 되도록 Expanded + Obx(ListView) 구성
        Expanded(
          child: Obx(() {
            if (viewModel.isLoading) {
              return const Center(child: CircularProgressIndicator());
            }

            final filteredList = viewModel.filteredList;

            if (filteredList.isEmpty) {
              return const Center(child: Text("해당 카테고리의 피드가 없습니다."));
            }

            return ListView.separated(
              controller: viewModel.scrollController,
              padding: const EdgeInsets.symmetric(horizontal: 20),
              itemCount: filteredList.length,
              separatorBuilder: (_, __) => const SizedBox(height: 24),
              itemBuilder: (context, index) {
                final feed = filteredList[index];
                return FeedCard(feed: feed, isMine: false);
              },
            );
          }),
        ),
      ],
    );
  }
}
