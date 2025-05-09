import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/views/feed/widgets/feed_card.dart';
import 'package:get/get.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/widgets/app_bar/back_app_bar.dart';
import 'package:greenap/views_model/my_feed_list/my_feed_list_view_model.dart';

class MyFeedListScreen extends BaseScreen<MyFeedListViewModel> {
  const MyFeedListScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return BackAppBar(title: "내 피드");
  }

  @override
  Widget buildBody(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Obx(() {
        if (viewModel.isLoading.value) {
          return const Center(child: CircularProgressIndicator());
        }

        if (viewModel.feedList.isEmpty) {
          return const Center(child: Text("해당 카테고리의 피드가 없습니다."));
        }

        return ListView.separated(
          clipBehavior: Clip.none,
          itemCount: viewModel.feedList.length,
          separatorBuilder: (_, __) => const SizedBox(height: 24),
          itemBuilder: (context, index) {
            final feed = viewModel.feedList[index];
            return FeedCard(feed: feed);
          },
        );
      }),
    );
  }
}
