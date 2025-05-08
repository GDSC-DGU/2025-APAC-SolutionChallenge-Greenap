import 'package:flutter/material.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';

class LoadingPopup extends StatelessWidget {
  const LoadingPopup({super.key});

  @override
  Widget build(BuildContext context) {
    return const BasePopupDialog(
      title: '챌린지 인증 진행중',
      subtitle: '업로드한 사진을 분석중입니다!\n잠시만 기다려주세요',
      child: SizedBox(
        height: 60,
        width: 60,
        child: CircularProgressIndicator(strokeWidth: 4),
      ),
    );
  }
}
