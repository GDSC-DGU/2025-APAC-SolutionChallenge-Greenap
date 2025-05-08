import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

enum CommonButtonStyleType { primary, outlined }

class CommonButton extends StatelessWidget {
  final String text;
  final VoidCallback onPressed;
  final CommonButtonStyleType type;

  const CommonButton._({
    super.key,
    required this.text,
    required this.onPressed,
    required this.type,
  });

  factory CommonButton.primary({
    required String text,
    required VoidCallback onPressed,
  }) {
    return CommonButton._(
      text: text,
      onPressed: onPressed,
      type: CommonButtonStyleType.primary,
    );
  }

  factory CommonButton.outlined({
    required String text,
    required VoidCallback onPressed,
  }) {
    return CommonButton._(
      text: text,
      onPressed: onPressed,
      type: CommonButtonStyleType.outlined,
    );
  }

  @override
  Widget build(BuildContext context) {
    final isPrimary = type == CommonButtonStyleType.primary;

    return TextButton(
      onPressed: onPressed,
      style: ButtonStyle(
        backgroundColor: WidgetStateProperty.all(
          isPrimary ? ColorSystem.mint : ColorSystem.white,
        ),
        foregroundColor: WidgetStateProperty.all(
          isPrimary ? ColorSystem.white : ColorSystem.mint,
        ),
        shape: WidgetStateProperty.all(
          RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(4),
            side: BorderSide(color: ColorSystem.mint, width: isPrimary ? 0 : 1),
          ),
        ),
        padding: WidgetStateProperty.all(
          EdgeInsets.symmetric(horizontal: 12, vertical: 4),
        ),
        minimumSize: WidgetStateProperty.all(Size.zero),
        tapTargetSize: MaterialTapTargetSize.shrinkWrap,
      ),
      child: Text(
        text,
        style: FontSystem.Button3.copyWith(
          color: isPrimary ? ColorSystem.white : ColorSystem.mint,
        ),
      ),
    );
  }
}
