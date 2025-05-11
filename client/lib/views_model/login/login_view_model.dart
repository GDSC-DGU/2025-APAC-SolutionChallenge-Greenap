import 'dart:convert';
import 'package:get/get.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;

class LoginViewModel extends GetxController {
  final GoogleSignIn _googleSignIn = GoogleSignIn(scopes: ['email', 'profile']);

  final isLoading = false.obs;

  Future<void> signInWithGoogle() async {
    isLoading.value = true;
    try {
      print("[DEBUG] 구글 로그인 시작");
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        print("[DEBUG] 사용자가 로그인 취소함");
        isLoading.value = false;
        return;
      }

      print("[DEBUG] 사용자 로그인 완료: ${googleUser.email}");

      final GoogleSignInAuthentication googleAuth =
          await googleUser.authentication;

      print("[DEBUG] accessToken: ${googleAuth.accessToken}");
      print("[DEBUG] idToken: ${googleAuth.idToken}");

      final String? code = googleAuth.idToken;
      if (code == null) {
        print("[ERROR] idToken이 null입니다. API 요청 불가능");
        isLoading.value = false;
        return;
      }

      final response = await http.get(
        Uri.parse(
          'https://greenap-server-590591992798.asia-northeast3.run.app/login/oauth2/google?code=${code}',
        ),
        headers: {'Content-Type': 'application/json'},
      );

      print("[DEBUG] 백엔드 응답 코드: ${response.statusCode}");
      print("[DEBUG] 백엔드 응답 내용: ${response.body}");

      if (response.statusCode == 200) {
        final responseData = jsonDecode(response.body);
        final token = responseData['token'];

        // TODO: Save token securely (e.g., with flutter_secure_storage)
        // TODO: Navigate to home screen

        print("로그인 성공! token: $token");
        Get.offAllNamed('/home');
      } else {
        print("로그인 실패: ${response.statusCode} ${response.body}");
      }
    } catch (e) {
      print("예외 발생: $e");
    } finally {
      isLoading.value = false;
    }
  }
}
