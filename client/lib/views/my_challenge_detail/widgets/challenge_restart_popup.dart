import 'package:flutter/material.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';
import 'package:greenap/domain/models/challenge_detail.dart';

class ChallengeRestartPopup extends StatelessWidget {
  final ChallengeDetailModel challenge;
  final int selectedDuration;
  final VoidCallback onChecked;

  const ChallengeRestartPopup({
    super.key,
    required this.challenge,
    required this.selectedDuration,
    required this.onChecked,
  });

  @override
  Widget build(BuildContext context) {
    return BasePopupDialog(
      title: '챌린지를 시작합니다!',
      subtitle:
          '${challenge.title}를 $selectedDuration일간 이어서 도전합니다. \n꾸준한 참여로 연속 참여일수를 늘려보아요!',
      actions: [
        PopupActionButton(
          text: '확인',
          type: ButtonStyleType.outlined,
          onPressed: onChecked,
        ),
      ],
    );
  }
}
