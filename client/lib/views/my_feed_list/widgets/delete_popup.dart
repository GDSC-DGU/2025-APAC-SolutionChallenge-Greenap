import 'package:flutter/material.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';

class FeedDeletePopup extends StatelessWidget {
  final VoidCallback onConfirmed;
  final VoidCallback onCanceled;

  const FeedDeletePopup({
    super.key,
    required this.onConfirmed,
    required this.onCanceled,
  });

  @override
  Widget build(BuildContext context) {
    return BasePopupDialog(
      title: '삭제하시겠습니까?',
      subtitle: '인증 피드를 삭제하게 되면\n안좋음',
      actions: [
        PopupActionButton(
          text: '취소',
          type: ButtonStyleType.outlined,
          onPressed: onCanceled,
        ),
        PopupActionButton(
          text: '삭제',
          type: ButtonStyleType.danger,
          onPressed: onConfirmed,
        ),
      ],
    );
  }
}
