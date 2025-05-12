import 'package:get/get.dart';
import 'package:greenap/config/app_config.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

abstract class BaseConnect extends GetConnect {
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  @override
  void onInit() {
    httpClient.baseUrl = AppConfig.baseUrl;
    httpClient.defaultContentType = 'application/json; charset=utf-8';
    httpClient.timeout = const Duration(seconds: 10);

    httpClient.addRequestModifier<Object?>((request) async {
      final token = await getAccessToken();
      if (token != null) {
        request.headers['Authorization'] = 'Bearer $token';
      }
      return request;
    });
  }

  Future<String?> getAccessToken() async {
    try {
      final token = await _secureStorage.read(key: 'accessToken');
      return token;
    } catch (e) {
      print('[ERROR] 토큰 가져오기 실패: $e');
      return null;
    }
  }
}
