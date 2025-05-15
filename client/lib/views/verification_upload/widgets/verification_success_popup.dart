import 'package:flutter/material.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';
import 'package:greenap/views_model/verification_upload/verification_upload_view_model.dart';
import 'package:get/get.dart';

class VerificationSuccessPopup extends StatelessWidget {
  final bool? isFinished;
  final VoidCallback onViewFeed;
  final VoidCallback onUploadFeed;
  final VoidCallback onViewReport;

  const VerificationSuccessPopup({
    super.key,
    this.isFinished,
    required this.onViewFeed,
    required this.onUploadFeed,
    required this.onViewReport,
  });

  @override
  Widget build(BuildContext context) {
    return (isFinished == true ? finishedPopup() : nomalPopup());
  }

  Widget nomalPopup() {
    return BasePopupDialog(
      title: '챌린지 인증을 완료했어요!',
      subtitle: '축하합니다!',
      actions: [
        PopupActionButton(
          text: '피드 둘러보기',
          onPressed: onViewFeed,
          type: ButtonStyleType.outlined,
        ),
        PopupActionButton(
          text: '피드 올리기',
          onPressed: onUploadFeed,
          type: ButtonStyleType.primary,
        ),
      ],
    );
  }

  Widget finishedPopup() {
    final viewModel = Get.find<VerificationUploadViewModel>();

    return BasePopupDialog(
      title: '챌린지 인증을 완료했어요!',
      subtitle: '해당 챌린지가 오늘부로 종료되었어요. \n지금 바로 리포트를 확인할 수 있습니다!',

      actions: [
        PopupActionButton(
          text: '리포트 보기',
          onPressed: onViewReport,
          type: ButtonStyleType.outlined,
        ),

        PopupActionButton(
          text: '피드 올리기',
          onPressed: onUploadFeed,
          type: ButtonStyleType.primary,
        ),
      ],
    );
  }
}
