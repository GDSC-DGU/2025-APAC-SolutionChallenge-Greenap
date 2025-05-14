import 'package:flutter/material.dart';
import 'package:greenap/config/Font_system.dart';
import 'package:greenap/config/color_system.dart';
import 'package:get/get.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:greenap/views_model/verification_upload/verification_upload_view_model.dart';
import 'package:greenap/widgets/app_bar/back_app_bar.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/widgets/common/bottom_action_button.dart';
import './widgets/verification_fail_popup.dart';
import './widgets/verification_success_popup.dart';
import './widgets/loading_popup.dart';
import 'package:image_picker/image_picker.dart';

class VerificationUploadScreen extends BaseScreen<VerificationUploadViewModel> {
  const VerificationUploadScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return BackAppBar(title: '인증하기');
  }

  @override
  Widget buildBody(BuildContext context) {
    return Obx(() {
      final challenge = viewModel.challengeDetail.value;

      if (challenge == null) {
        return const Center(child: CircularProgressIndicator());
      }

      return SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            _buildTitle(challenge),
            const SizedBox(height: 24),
            _buildDescriptionNotice(),
            const SizedBox(height: 24),
            _buildChallengeImage(challenge),
            const SizedBox(height: 24),
            _buildMethodCard(challenge),
            const SizedBox(height: 24),
            _buildCheckBox(),
            const SizedBox(height: 12),
            BottomActionButton.outlined(
              text: '인증 사진 첨부하기',
              onPressed:
                  viewModel.isChecked.value
                      ? () => _handleAttachPhoto(viewModel)
                      : null,
            ),
          ],
        ),
      );
    });
  }

  Future<void> _handleAttachPhoto(VerificationUploadViewModel viewModel) async {
    await viewModel.pickImage();

    if (viewModel.selectedImage.value == null) return;

    showDialog(
      context: Get.context!,
      barrierDismissible: false,
      builder: (_) => const LoadingPopup(),
    );

    final resultMessage = await viewModel.submitVerification();

    Navigator.of(Get.context!, rootNavigator: true).pop();

    await Future.delayed(const Duration(milliseconds: 100));

    final isSuccess = resultMessage == "Success";

    await showDialog(
      context: Get.context!,
      builder:
          (_) =>
              isSuccess
                  ? VerificationSuccessPopup(
                    onViewFeed: () {
                      Navigator.pop(Get.context!);
                      Get.offAllNamed('/root', arguments: {'initialTab': 1});
                    },
                    onUploadFeed: () {
                      Navigator.pop(Get.context!);
                      Get.toNamed(
                        '/feed-post',
                        arguments: {
                          'imageUrl': viewModel.uploadedImageUrl.value,
                          'userChallengeId': viewModel.userChallengeId,
                        },
                      );
                    },
                  )
                  : VerificationFailPopup(
                    reason: '재 인증이 필요합니다.',
                    onViewFeed: () {
                      Navigator.pop(Get.context!);
                      Get.offAllNamed('/root', arguments: {'initialTab': 1});
                    },
                    onRetry: () {
                      Navigator.pop(Get.context!);
                    },
                  ),
    );
  }

  Widget _buildTitle(ChallengeDetailModel challenge) {
    return Text(
      challenge.title,
      style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
    );
  }

  Widget _buildDescriptionNotice() {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
      decoration: BoxDecoration(
        color: ColorSystem.mint.withOpacity(0.04),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        '챌린지 인증 방법에 맞도록 사진을 찍어 올리면, Gemini가 사진을 분석하여 인증 여부를 확인합니다! 아래 설명을 확인하고 인증을 진행해보아요!',
        style: FontSystem.Body3.copyWith(color: ColorSystem.gray[700]),
      ),
    );
  }

  Widget _buildChallengeImage(ChallengeDetailModel challenge) {
    return AspectRatio(
      aspectRatio: 1,
      child: ClipRRect(
        borderRadius: BorderRadius.circular(4),
        child: Image.network(
          challenge.certificationExampleImageUrl,
          fit: BoxFit.cover,
          width: double.infinity,
        ),
      ),
    );
  }

  Widget _buildMethodCard(ChallengeDetailModel challenge) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(4),
        color: ColorSystem.white,
        boxShadow: [
          BoxShadow(
            color: ColorSystem.mint.withOpacity(0.1),
            spreadRadius: 2,
            blurRadius: 4,
            offset: Offset(0, 0),
          ),
        ],
      ),
      child: Row(
        children: [
          SvgPicture.asset('assets/icons/camera.svg', width: 16, height: 16),
          const SizedBox(width: 6),
          Expanded(
            child: Text(
              challenge.certificationMethodDescription,
              style: FontSystem.Body3.copyWith(color: ColorSystem.mint),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCheckBox() {
    return Row(
      children: [
        Obx(
          () => Checkbox(
            value: viewModel.isChecked.value,
            onChanged: (value) => viewModel.isChecked.value = value ?? false,
            activeColor: ColorSystem.mint,
            materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
            visualDensity: VisualDensity.compact,
          ),
        ),
        const SizedBox(width: 8),
        Text(
          '사진 인증 방법을 확인하였습니다.',
          style: FontSystem.Body3.copyWith(color: ColorSystem.gray[800]),
        ),
      ],
    );
  }
}
