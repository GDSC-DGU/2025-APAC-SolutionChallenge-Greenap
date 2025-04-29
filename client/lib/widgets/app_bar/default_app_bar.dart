import 'package:flutter/material.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/config/color_system.dart';

class DefaultAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;

  const DefaultAppBar({super.key, required this.title});

  @override
  Size get preferredSize => const Size.fromHeight(50);

  Widget build(BuildContext context) {
    return AppBar(
      title: Padding(
        padding: const EdgeInsets.all(12),
        child: Text(title, style: FontSystem.Head2),
      ),
      surfaceTintColor: ColorSystem.white,
      backgroundColor: ColorSystem.white,
      centerTitle: true,
      automaticallyImplyLeading: false,
      elevation: 0,
    );
  }
}
