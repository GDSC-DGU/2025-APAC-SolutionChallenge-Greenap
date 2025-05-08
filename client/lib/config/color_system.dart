import 'package:flutter/material.dart';

abstract class ColorSystem {
  static const white = Color(0xFFFFFFFF);
  static const black = Color(0xFF151515);
  static const mint = Color(0xFF00D6A5);
  static const red = Color(0xFFF62C2C);
  static const MaterialColor gray = MaterialColor(_grayValue, <int, Color>{
    900: Color(0xFF1D1D26),
    800: Color(0xFF35363F),
    700: Color(0xFF4E4F58),
    600: Color(0xFF676871),
    500: Color(0xFF808189),
    400: Color(0xFF9999A1),
    300: Color(0xFFB1B1BA),
    200: Color(0xFFC9CAD3),
    100: Color(0xFFE2E3EC),
    50: Color(0xFFF1F2FB),
  });
  static const int _grayValue = 0xFF808189;
  static const mainGradient = RadialGradient(
    center: Alignment.center,
    radius: 0.5,
    colors: [Color(0xFF4EC9EB), Color(0xFF4FDAC1), Color(0xFF3CBBFD)],
    stops: [0.0, 0.81, 1.0],
  );
  static const blueGradient = LinearGradient(
    colors: [Color(0xFFD3FFF7), Color(0xFFFFFFFF)],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0.0, 1.0],
  );
  static const greenGradient = LinearGradient(
    colors: [Color(0xFFF3FFE7), Color(0xFFFFFFFF)],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0.0, 1.0],
  );
  static const yellowGradient = LinearGradient(
    colors: [Color(0xFFFFFDBF), Color(0xFFFFFFFF)],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0.0, 1.0],
  );
  static const pinkGradient = LinearGradient(
    colors: [Color(0xFFFFECFB), Color(0xFFFFFFFF)],
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    stops: [0.0, 1.0],
  );
}
