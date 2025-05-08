import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

class BottomActionButton extends StatelessWidget {
  final String text;
  final VoidCallback? onPressed;
  final bool isOutlined;

  const BottomActionButton._({
    required this.text,
    this.onPressed,
    required this.isOutlined,
    super.key,
  });

  const BottomActionButton.primary({
    required String text,
    required VoidCallback? onPressed,
    Key? key,
  }) : this._(text: text, onPressed: onPressed, isOutlined: false, key: key);

  const BottomActionButton.outlined({
    required String text,
    required VoidCallback? onPressed,
    Key? key,
  }) : this._(text: text, onPressed: onPressed, isOutlined: true, key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: EdgeInsets.zero,
      child: ElevatedButton(
        style: ButtonStyle(
          backgroundColor: WidgetStateProperty.resolveWith<Color>((states) {
            if (states.contains(WidgetState.disabled)) {
              return isOutlined ? Colors.white : ColorSystem.gray[300]!;
            }
            return isOutlined ? Colors.white : ColorSystem.mint;
          }),
          foregroundColor: WidgetStateProperty.resolveWith<Color>((states) {
            if (states.contains(WidgetState.disabled)) {
              return ColorSystem.gray[300]!;
            }
            return isOutlined ? ColorSystem.mint : Colors.white;
          }),
          side: WidgetStateProperty.resolveWith<BorderSide?>((states) {
            if (isOutlined) {
              return BorderSide(
                color:
                    states.contains(WidgetState.disabled)
                        ? ColorSystem.gray[300]!
                        : ColorSystem.mint,
                width: 1.5,
              );
            }
            return null;
          }),
          shape: WidgetStateProperty.all<RoundedRectangleBorder>(
            RoundedRectangleBorder(borderRadius: BorderRadius.circular(6)),
          ),
          elevation: WidgetStateProperty.all(0),
        ),
        onPressed: onPressed,
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Text(text, style: FontSystem.Button1),
        ),
      ),
    );
  }
}
