import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';

class UseIcePopup extends StatelessWidget {
  final VoidCallback onClose;
  final VoidCallback onUse;

  const UseIcePopup({super.key, required this.onClose, required this.onUse});

  @override
  Widget build(BuildContext context) {
    return BasePopupDialog(
      child: _content(),
      title: '얼리기 기능을\n사용하시겠습니까?',

      actions: [
        PopupActionButton(
          text: '닫기',
          onPressed: onClose,
          type: ButtonStyleType.disabled,
        ),
        PopupActionButton(
          text: '사용하기',
          onPressed: onUse,
          type: ButtonStyleType.primary,
        ),
      ],
    );
  }

  Widget _content() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        SvgPicture.asset('assets/icons/ice.svg', width: 70, height: 70),
        SizedBox(height: 20),
        Text(
          '지금 사용 시, 오늘의 인증이 면제되고 \n연속 도전 일수를 유지할 수 있습니다!',
          style: FontSystem.Body2.copyWith(color: ColorSystem.gray[700]),
        ),
      ],
    );
  }
}
