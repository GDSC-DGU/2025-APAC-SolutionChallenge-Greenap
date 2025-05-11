import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
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

  final RxString selectedCategory = '전체'.obs;
  final categories = ['전체', '자원절약', '교통절감', '재사용', '친환경'];

  @override
  Widget buildBody(BuildContext context) {
    return Column(
      children: [
        const SizedBox(height: 20),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: FeedCategoryFilter(
            selectedCategory: selectedCategory,
            categories: categories,
          ),
        ),
        const SizedBox(height: 12),
        Expanded(
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Obx(() {
              if (viewModel.isLoading.value) {
                return const Center(child: CircularProgressIndicator());
              }

              final filteredList =
                  selectedCategory.value == '전체'
                      ? viewModel.feedList
                      : viewModel.feedList
                          .where(
                            (item) => item.category == selectedCategory.value,
                          )
                          .toList();

              if (filteredList.isEmpty) {
                return const Center(child: Text("해당 카테고리의 피드가 없습니다."));
              }

              return ListView.separated(
                clipBehavior: Clip.none,
                itemCount: filteredList.length,
                separatorBuilder: (_, __) => const SizedBox(height: 24),
                itemBuilder: (context, index) {
                  final feed = filteredList[index];
                  return FeedCard(feed: feed);
                },
              );
            }),
          ),
        ),
      ],
    );
  }
}
