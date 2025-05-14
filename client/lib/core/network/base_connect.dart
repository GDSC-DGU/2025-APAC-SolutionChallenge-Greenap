import 'package:get/get.dart';
import 'package:greenap/config/app_config.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

abstract class BaseConnect extends GetConnect {
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  @override
  void onInit() {
    print('[DEBUG] BaseConnect 초기화 - baseUrl: ${AppConfig.baseUrl}'); // 👈 추가

    httpClient.baseUrl = AppConfig.baseUrl;
    httpClient.defaultContentType = 'application/json; charset=utf-8';
    httpClient.timeout = const Duration(seconds: 10);

    httpClient.addRequestModifier<Object?>((request) async {
      final token = await getAccessToken();
      if (token != null) {
        request.headers['Authorization'] = 'Bearer $token';
      } else {
        print('[DEBUG] 요청 헤더에 토큰 없음');
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

  Future<bool> refreshToken() async {
    final refreshToken = await _secureStorage.read(key: 'refreshToken');
    if (refreshToken == null) return false;

    final response = await post('/api/v1/auth/refresh', {
      'refreshToken': refreshToken,
    });

    if (response.statusCode == 200) {
      final newToken = response.body['data']['accessToken'];
      await _secureStorage.write(key: 'accessToken', value: newToken);
      print('[DEBUG] 토큰 갱신 완료');
      return true;
    } else {
      await _secureStorage.deleteAll();
      Get.offAllNamed('/login');
      return false;
    }
  }

  /// 안전한 GET 요청
  Future<Response> getRequest(String url, {Map<String, dynamic>? query}) async {
    var response = await get(url, query: query);

    if (_shouldRefresh(response)) {
      final success = await refreshToken();
      if (success) {
        response = await get(url, query: query);
      }
    }

    return response;
  }

  /// 안전한 POST 요청
  Future<Response> postRequest(String url, dynamic? body) async {
    var response = await post(url, body);

    if (_shouldRefresh(response)) {
      final success = await refreshToken();
      if (success) {
        response = await post(url, body);
      }
    }

    return response;
  }

  /// 토큰 갱신이 필요한 응답인지 확인
  bool _shouldRefresh(Response response) {
    final is401 = response.statusCode == 401;
    final isExpiredMessage =
        response.body is Map &&
        (response.body['message'] == 'Expired Token Error' ||
            response.body['error'] == 'Expired Token Error');
    return is401 || isExpiredMessage;
  }
}
