import 'package:flutter/material.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';

class VerificationSuccessPopup extends StatelessWidget {
  final VoidCallback onViewFeed;
  final VoidCallback onUploadFeed;

  const VerificationSuccessPopup({
    super.key,
    required this.onViewFeed,
    required this.onUploadFeed,
  });

  @override
  Widget build(BuildContext context) {
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
}
