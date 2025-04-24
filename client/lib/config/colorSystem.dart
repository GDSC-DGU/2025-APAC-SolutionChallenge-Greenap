import 'package:flutter/material.dart';

abstract class ColorSystem {
  /// Transparent Color
  static const Color transparent = Colors.transparent;

  /// White Color
  static const Color white = Color(0xFFFFFFFF);

  /// Black Color
  static const Color black = Color(0xFF151515);

  /// Primary Color
  static const MaterialColor mint = MaterialColor(_primaryValue, <int, Color>{
    900: Color(0xFF006D5B),
    800: Color(0xFF008066),
    700: Color(0xFF009C77),
    600: Color(0xFF00B88C),
    500: Color(0xFF00D6A5),
    400: Color(0xFF33E0B1),
    300: Color(0xFF66E9BE),
    200: Color(0xFF99F1CD),
    100: Color(0xFFCCF8E0),
    50: Color(0xFFE6FCF0),
  });
  static const int _primaryValue = 0xFF2DDABB;

  /// Gray Color
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
}
