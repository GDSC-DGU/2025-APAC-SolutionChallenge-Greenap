import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';

class BackAppBar extends StatelessWidget implements PreferredSizeWidget {
  const BackAppBar({super.key, required this.title, this.onBackPress});

  final String title;
  final VoidCallback? onBackPress;

  @override
  Size get preferredSize => const Size.fromHeight(45);

  Widget build(BuildContext context) {
    return AppBar(
      toolbarHeight: preferredSize.height,
      title: Padding(
        padding: const EdgeInsets.all(10),
        child: Text(
          title,
          style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
        ),
      ),
      centerTitle: true,
      backgroundColor: ColorSystem.white,
      surfaceTintColor: ColorSystem.white,
      automaticallyImplyLeading: false,
      titleSpacing: 0,
      leadingWidth: 50,
      leading: IconButton(
        icon: SvgPicture.asset(
          "assets/icons/left_arrow.svg",
          width: 24,
          height: 24,
        ),
        onPressed: onBackPress ?? () => Get.back(),
      ),
      elevation: 0,
    );
  }
}
