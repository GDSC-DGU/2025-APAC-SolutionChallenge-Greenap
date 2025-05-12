import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:greenap/config/app_routes.dart';
import 'package:greenap/config/app_pages.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:greenap/bindings/app_binding.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env");
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
      initialRoute: AppRoutes.SPLASH,
      getPages: AppPages.data,
      debugShowCheckedModeBanner: false,
      initialBinding: AppBinding(),
    );
  }
}
