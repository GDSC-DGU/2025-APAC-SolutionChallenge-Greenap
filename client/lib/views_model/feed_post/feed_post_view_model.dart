import 'package:get/get.dart';
import 'package:flutter/material.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';
import 'package:greenap/core/network/response_wrapper.dart';

class FeedPostViewModel extends GetxController {
  final TextEditingController contentController = TextEditingController();
  final RxInt contentLength = 0.obs;
  late final FeedProvider _provider;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<FeedProvider>();
    contentController.addListener(() {
      contentLength.value = contentController.text.length;
    });
  }

  Future<int?> postFeed({
    required int userChallengeId,
    required String imageUrl,
    required String content,
    required String publishDate,
  }) async {
    final response = await _provider.postFeed(
      userChallengeId,
      imageUrl,
      content,
      publishDate,
    );

    if (response.data != null) {
      return response.data;
    } else {
      print('[ERROR]: ${response.message}');
      return null;
    }
  }
}
