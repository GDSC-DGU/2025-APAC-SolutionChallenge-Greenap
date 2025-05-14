import 'package:flutter/material.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';

class FeedPostCompletePopup extends StatelessWidget {
  final VoidCallback onConfirm;
  final VoidCallback onGoToFeed;

  const FeedPostCompletePopup({
    super.key,
    required this.onConfirm,
    required this.onGoToFeed,
  });

  @override
  Widget build(BuildContext context) {
    return BasePopupDialog(
      title: '피드 작성 완료!',
      subtitle: '사진을 피드에 업로드 했습니다!\n지금 바로 확인해보세요!',
      actions: [
        PopupActionButton(
          text: '확인',
          onPressed: onConfirm,
          type: ButtonStyleType.disabled,
        ),
        PopupActionButton(
          text: '피드 둘러보기',
          onPressed: onGoToFeed,
          type: ButtonStyleType.primary,
        ),
      ],
    );
  }
}
