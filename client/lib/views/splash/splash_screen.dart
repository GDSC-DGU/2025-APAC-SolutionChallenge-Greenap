import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:greenap/config/color_system.dart';
import 'package:lottie/lottie.dart';
import 'dart:async';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  bool _animationLoaded = false;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: ColorSystem.mainGradient),
        child: Center(
          child: Lottie.asset(
            'assets/lottie/logo_animation.json',
            width: 350,
            height: 350,
            fit: BoxFit.contain,
            onLoaded: (composition) {
              // 애니메이션이 다 로드된 후 2.5초 후 이동
              if (!_animationLoaded) {
                _animationLoaded = true;
                Future.delayed(composition.duration, () {
                  Get.offNamed('/login');
                });
              }
            },
          ),
        ),
      ),
    );
  }
}
