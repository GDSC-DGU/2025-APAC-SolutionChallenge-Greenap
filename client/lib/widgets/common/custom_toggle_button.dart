import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

class CustomToggleButton extends StatefulWidget {
  final String leftText;
  final String rightText;
  final Function(bool isLeftSelected) onToggle;

  const CustomToggleButton({
    super.key,
    required this.leftText,
    required this.rightText,
    required this.onToggle,
  });

  @override
  State<CustomToggleButton> createState() => _CustomToggleButtonState();
}

class _CustomToggleButtonState extends State<CustomToggleButton> {
  bool isLeftSelected = true;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: ColorSystem.mint.withOpacity(0.08),
        borderRadius: BorderRadius.circular(32),
      ),
      child: IntrinsicWidth(
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            _buildToggleButton(
              text: widget.leftText,
              isSelected: isLeftSelected,
              onTap: () {
                setState(() => isLeftSelected = true);
                widget.onToggle(true);
              },
            ),
            _buildToggleButton(
              text: widget.rightText,
              isSelected: !isLeftSelected,
              onTap: () {
                setState(() => isLeftSelected = false);
                widget.onToggle(false);
              },
            ),
          ],
        ),
      ),
    );
  }
}

Widget _buildToggleButton({
  required String text,
  required bool isSelected,
  required VoidCallback onTap,
}) {
  return GestureDetector(
    onTap: onTap,
    child: Container(
      padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 20),
      decoration: BoxDecoration(
        color: isSelected ? ColorSystem.white : Colors.transparent,
        borderRadius: BorderRadius.circular(32),
      ),
      alignment: Alignment.center,
      child: Text(
        text,
        style: FontSystem.Button2.copyWith(color: ColorSystem.gray[700]),
      ),
    ),
  );
}
