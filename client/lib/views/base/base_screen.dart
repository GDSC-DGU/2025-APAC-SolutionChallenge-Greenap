import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:greenap/config/color_system.dart';

@immutable
abstract class BaseScreen<T extends GetxController> extends GetView<T> {
  const BaseScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBody: extendBodyBehindAppBar,
      appBar: buildAppBar(context),
      body: Container(
        decoration: useGradientBackground ? gradientBackground : null,
        color: useGradientBackground ? null : ColorSystem.white,

        child: SafeArea(
          top: applyTopSafeArea,
          bottom: applyBottomSafeArea,
          child: SafeArea(
            top: applyTopSafeArea,
            bottom: applyBottomSafeArea,
            child: buildBody(context),
          ),
        ),
      ),
      bottomNavigationBar: buildBottomNavigationBar(context),
    );
  }

  /// 뷰모델
  @protected
  T get viewModel => controller;

  PreferredSizeWidget? buildAppBar(BuildContext context) => null;

  @protected
  Widget buildBody(BuildContext context);

  @protected
  Widget? buildBottomNavigationBar(BuildContext context) => null;

  @protected
  bool get applyTopSafeArea => true;

  @protected
  bool get applyBottomSafeArea => false;

  @protected
  bool get useGradientBackground => false;

  @protected
  bool get extendBodyBehindAppBar => false;

  @protected
  BoxDecoration? get gradientBackground => BoxDecoration(
    gradient: LinearGradient(
      colors: [ColorSystem.mint.withOpacity(0.2), ColorSystem.white],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
    ),
  );
}
