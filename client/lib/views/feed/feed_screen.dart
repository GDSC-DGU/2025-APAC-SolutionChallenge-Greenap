import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/views_model/feed_view_model.dart';
import 'package:get/get.dart';

class FeedScreen extends GetView<FeedViewModel> {
  const FeedScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: Center(child: Text('피드 페이지')),
    );
  }
}
