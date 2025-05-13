import 'package:flutter/material.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';
import 'package:greenap/domain/models/challenge_detail.dart';

class ChallengeStartPopup extends StatelessWidget {
  final ChallengeDetailModel challenge;
  final int selectedDuration;
  final VoidCallback onChecked;
  final VoidCallback goVerification;

  const ChallengeStartPopup({
    super.key,
    required this.challenge,
    required this.selectedDuration,
    required this.onChecked,
    required this.goVerification,
  });

  @override
  Widget build(BuildContext context) {
    return BasePopupDialog(
      title: '${challenge.title} \n$selectedDuration일 도전!',
      subtitle: '매일매일 챌린지를 인증해보세요.\n오늘부터 함께해요',
      actions: [
        PopupActionButton(
          text: '확인',
          type: ButtonStyleType.disabled,
          onPressed: onChecked,
        ),
        PopupActionButton(
          text: '인증하러 가기',
          type: ButtonStyleType.primary,
          onPressed: goVerification,
        ),
      ],
    );
  }
}
