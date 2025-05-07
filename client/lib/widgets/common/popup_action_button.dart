import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

enum ButtonStyleType {
  primary, // 민트색 배경
  outlined, // 민트색 테두리
  disabled, // 회색 테두리
  danger, // 빨간 테두리
}

class PopupActionButton extends StatelessWidget {
  final String text;
  final VoidCallback onPressed;
  final ButtonStyleType type;

  const PopupActionButton({
    super.key,
    required this.text,
    required this.onPressed,
    this.type = ButtonStyleType.primary,
  });

  @override
  Widget build(BuildContext context) {
    final style = _getStyle(type);

    return SizedBox(
      child: TextButton(
        onPressed: onPressed,
        style: ButtonStyle(
          backgroundColor: MaterialStateProperty.all(style['background']),
          side: style['border'],
          shape: MaterialStateProperty.all(
            RoundedRectangleBorder(borderRadius: BorderRadius.circular(4)),
          ),
          padding: MaterialStateProperty.all(
            const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          ),
        ),
        child: Text(
          text,
          style: FontSystem.Button2.copyWith(color: style['text']),
        ),
      ),
    );
  }

  Map<String, dynamic> _getStyle(ButtonStyleType type) {
    switch (type) {
      case ButtonStyleType.primary:
        return {
          'background': ColorSystem.mint,
          'text': ColorSystem.white,
          'border': null,
        };
      case ButtonStyleType.outlined:
        return {
          'background': ColorSystem.white,
          'text': ColorSystem.mint,
          'border': MaterialStateProperty.all(
            BorderSide(color: ColorSystem.mint),
          ),
        };
      case ButtonStyleType.disabled:
        return {
          'background': ColorSystem.white,
          'text': ColorSystem.gray[300],
          'border': MaterialStateProperty.all(
            BorderSide(color: ColorSystem.gray[300]!),
          ),
        };
      case ButtonStyleType.danger:
        return {
          'background': ColorSystem.white,
          'text': Colors.red,
          'border': MaterialStateProperty.all(
            const BorderSide(color: Colors.red),
          ),
        };
    }
  }
}
