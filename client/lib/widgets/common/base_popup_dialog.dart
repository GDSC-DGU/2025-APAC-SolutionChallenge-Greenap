import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

class BasePopupDialog extends StatelessWidget {
  final String? title;
  final String? subtitle;
  final Widget? child;
  final List<Widget>? actions;

  const BasePopupDialog({
    super.key,
    this.title,
    this.subtitle,
    this.child,
    this.actions,
  });
  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: IntrinsicHeight(child: _buildContent()),
      ),
    );
  }

  Widget _buildContent() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        if (title != null) _buildTitle(),
        if (subtitle != null) _buildSubtitle(),
        if (child != null) ...[_buildChild()],
        if (actions != null && actions!.isNotEmpty) _buildActions(),
      ],
    );
  }

  Widget _buildTitle() {
    return Column(
      children: [
        Text(
          title!,
          style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  Widget _buildSubtitle() {
    return Column(
      children: [
        const SizedBox(height: 20),
        Text(
          subtitle!,
          style: FontSystem.Body2.copyWith(color: ColorSystem.gray[700]),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  Widget _buildChild() {
    return Padding(padding: const EdgeInsets.only(top: 20), child: child!);
  }

  Widget _buildActions() {
    return Padding(
      padding: const EdgeInsets.only(top: 20),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        mainAxisSize: MainAxisSize.min,
        children: [
          for (int i = 0; i < actions!.length; i++) ...[
            if (i != 0) const SizedBox(width: 12),
            actions![i],
          ],
        ],
      ),
    );
  }
}
