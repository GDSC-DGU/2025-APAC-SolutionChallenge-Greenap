import 'dart:convert';
import 'package:get/get.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:greenap/data/dto/user_info_dto.dart';
import 'package:greenap/data/provider/auth/auth_provider.dart';
import 'package:greenap/core/network/response_wrapper.dart';

class LoginViewModel extends GetxController {
  final GoogleSignIn _googleSignIn = GoogleSignIn(scopes: ['email', 'profile']);
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();
  final isLoading = false.obs;
  late final AuthProvider _authProvider;

  @override
  void onInit() {
    super.onInit();
    _authProvider = Get.find<AuthProvider>();
    _checkLoginStatus();
  }

  Future<void> _checkLoginStatus() async {
    final accessToken = await _secureStorage.read(key: 'accessToken');
    final refreshToken = await _secureStorage.read(key: 'refreshToken');

    if (accessToken != null && refreshToken != null) {
      print('이미 로그인된 사용자입니다.');
      Get.offAllNamed('/root', arguments: {'initialTab': 2});
    }
  }

  Future<void> signInWithGoogle() async {
    isLoading.value = true;
    try {
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        isLoading.value = false;
        return;
      }

      final GoogleSignInAuthentication googleAuth =
          await googleUser.authentication;
      final accessToken = googleAuth.accessToken;
      final idToken = googleAuth.idToken;

      if (accessToken == null || idToken == null) {
        throw Exception("구글 토큰이 유효하지 않습니다.");
      }

      final ResponseWrapper<UserInfoDto> response = await _authProvider
          .googleLogin(accessToken, idToken);
      if (response.data != null) {
        final userModel = response.data!.toModel();

        await _secureStorage.write(
          key: 'accessToken',
          value: response.data!.jwtTokenDto.accessToken,
        );
        await _secureStorage.write(
          key: 'refreshToken',
          value: response.data!.jwtTokenDto.refreshToken,
        );

        await _secureStorage.write(key: 'nickname', value: userModel.nickname);

        await _secureStorage.write(
          key: 'profileImageUrl',
          value: userModel.profileImageUrl,
        );

        final savedAccessToken = await _secureStorage.read(key: 'accessToken');
        print('저장된 accessToken: $savedAccessToken');

        print("로그인 성공: ${userModel.nickname}");
        Get.offAllNamed('/root', arguments: {'initialTab': 2});
      } else {
        print("[ERROR] 로그인 실패 전체 응답: ${jsonEncode(response)}");
        throw Exception("로그인 실패: ${response.message ?? '응답 메시지 없음'}");
      }
    } catch (e) {
      print(" 로그인 예외 발생: $e");
    } finally {
      isLoading.value = false;
    }
  }
}
