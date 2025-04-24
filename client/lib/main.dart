import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:greenap/config/colorSystem.dart';
import 'package:greenap/config/appRoutes.dart';
import 'package:greenap/config/appPages.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      title: "Greenap",
      theme: ThemeData(useMaterial3: true, fontFamily: 'Pretendard'),
      // Initial Route
      initialRoute: AppRoutes.ROOT,
      getPages: AppPages.data,
    );
  }
}
