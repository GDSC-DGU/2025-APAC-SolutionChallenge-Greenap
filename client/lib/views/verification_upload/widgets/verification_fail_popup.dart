import 'package:flutter/material.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';

class VerificationFailPopup extends StatelessWidget {
  final VoidCallback onViewFeed;
  final VoidCallback onRetry;

  const VerificationFailPopup({
    super.key,
    required this.onViewFeed,
    required this.onRetry,
  });

  @override
  Widget build(BuildContext context) {
    return BasePopupDialog(
      title: '챌린지 인증을 실패했습니다',
      subtitle: '재인증이 필요합니다',
      actions: [
        PopupActionButton(
          text: '피드 둘러보기',
          onPressed: onViewFeed,
          type: ButtonStyleType.outlined,
        ),
        PopupActionButton(
          text: '재인증하기',
          onPressed: onRetry,
          type: ButtonStyleType.primary,
        ),
      ],
    );
  }
}
