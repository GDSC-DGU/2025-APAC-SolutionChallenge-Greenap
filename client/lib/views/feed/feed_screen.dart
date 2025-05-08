import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/views_model/feed/feed_view_model.dart';
import 'package:get/get.dart';
import 'package:greenap/widgets/app_bar/default_app_bar.dart';
import 'package:greenap/views/base/base_screen.dart';

class FeedScreen extends BaseScreen<FeedViewModel> {
  const FeedScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return DefaultAppBar(title: '챌린지 피드');
  }

  @override
  Widget buildBody(BuildContext context) {
    return Scaffold(
      backgroundColor: ColorSystem.white,
      body: Center(child: Text('피드 페이지')),
    );
  }
}
