import 'package:flutter/material.dart';
import 'package:greenap/widgets/app_bar/back_app_bar.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/views_model/feed_post/feed_post_view_model.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/widgets/common/bottom_action_button.dart';
import './widgets/feed_post_complete_popup.dart';
import 'package:get/get.dart';

class FeedPostScreen extends BaseScreen<FeedPostViewModel> {
  FeedPostScreen({super.key});
  late final String imageUrl;
  late final int userChallengeId;

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return BackAppBar(title: '글쓰기');
  }

  @override
  Widget buildBody(BuildContext context) {
    final args = Get.arguments as Map<String, dynamic>;
    imageUrl = args['imageUrl'] ?? '';
    userChallengeId = args['userChallengeId'] ?? 0;

    return Column(
      children: [
        if (imageUrl != null)
          Container(
            margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
            width: double.infinity,
            height: 400,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(12),
              image: DecorationImage(
                image: NetworkImage(imageUrl),
                fit: BoxFit.cover,
              ),
            ),
          ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 12,
                ),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: ColorSystem.gray[200]!),
                  color: ColorSystem.white,
                ),
                child: TextField(
                  controller: viewModel.contentController,
                  maxLines: 6,
                  maxLength: 1000,
                  decoration: InputDecoration.collapsed(
                    hintText: '챌린지 인증 내용을 작성해주세요.',
                    hintStyle: FontSystem.Body2.copyWith(
                      color: ColorSystem.gray[400],
                    ),
                  ),
                  style: FontSystem.Body2.copyWith(
                    color: ColorSystem.gray[700],
                  ),
                  onChanged: (_) => viewModel.update(),
                ),
              ),
              const SizedBox(height: 4),
              Obx(() {
                return Text(
                  '${viewModel.contentLength.value}/1,000',
                  style: FontSystem.Caption.copyWith(
                    color: ColorSystem.gray[400],
                  ),
                );
              }),
            ],
          ),
        ),
        const Spacer(),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
          child: BottomActionButton.primary(
            text: '작성 완료',
            onPressed: () async {
              final content = viewModel.contentController.text.trim();
              final publishDate =
                  DateTime.now().toIso8601String().split('T').first;

              final response = await viewModel.postFeed(
                userChallengeId: userChallengeId,
                imageUrl: imageUrl,
                content: content,
                publishDate: publishDate,
              );

              if (response != null) {
                await showDialog(
                  context: Get.context!,
                  barrierDismissible: false,
                  builder:
                      (_) => FeedPostCompletePopup(
                        onConfirm: () => Get.back(),
                        onGoToFeed: () {
                          Get.offAllNamed(
                            '/root',
                            arguments: {'initialTab': 1},
                          );
                        },
                      ),
                );
              } else {
                Get.snackbar(
                  '업로드 실패',
                  '알 수 없는 오류가 발생했습니다.',
                  snackPosition: SnackPosition.BOTTOM,
                  backgroundColor: Colors.red[50],
                  colorText: Colors.red[800],
                );
              }
            },
          ),
        ),
      ],
    );
  }
}
