import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/views/feed/widgets/feed_card.dart';

class FeedView extends StatelessWidget {
  final RxList<FeedItemModel> feeds;

  const FeedView({super.key, required this.feeds});

  @override
  Widget build(BuildContext context) {
    if (feeds.isEmpty) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 20),
        child: Center(
          child: Text(
            "아직 인증 피드가 없습니다.",
            style: TextStyle(color: ColorSystem.gray[700]),
          ),
        ),
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Text(
          "나의 인증 내역",
          style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
        ),
        const SizedBox(height: 12),
        ListView.separated(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: feeds.length,
          itemBuilder: (context, index) {
            return FeedCard(feed: feeds[index]);
          },
          separatorBuilder: (context, index) => const SizedBox(height: 12),
        ),
      ],
    );
  }
}
