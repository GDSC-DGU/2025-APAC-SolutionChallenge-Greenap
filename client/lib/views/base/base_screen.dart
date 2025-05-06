import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:greenap/config/color_system.dart';

@immutable
abstract class BaseScreen<T extends GetxController> extends GetView<T> {
  const BaseScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: useGradientBackground ? gradientBackground : null,
      color: useGradientBackground ? null : ColorSystem.white,
      child: SafeArea(
        top: applyTopSafeArea,
        bottom: applyBottomSafeArea,
        child: Column(
          children: [
            if (buildAppBar(context) != null) buildAppBar(context)!,
            Expanded(child: buildBody(context)),
            if (buildBottomWidget(context) != null) buildBottomWidget(context)!,
          ],
        ),
      ),
    );
  }

  /// 뷰모델
  @protected
  T get viewModel => controller;

  PreferredSizeWidget? buildAppBar(BuildContext context) => null;

  @protected
  Widget buildBody(BuildContext context);

  @protected
  Widget? buildBottomWidget(BuildContext context) => null;

  @protected
  bool get applyTopSafeArea => true;

  @protected
  bool get applyBottomSafeArea => false;

  @protected
  bool get useGradientBackground => false;

  @protected
  BoxDecoration? get gradientBackground => BoxDecoration(
    gradient: LinearGradient(
      colors: [ColorSystem.mint.withOpacity(0.2), ColorSystem.white],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
    ),
  );
}
