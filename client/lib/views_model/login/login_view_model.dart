import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;

class LoginViewModel extends GetxController {
  final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes: ['email', 'name', 'profile'],
  );

  final isLoading = false.obs;

  Future<void> signInWithGoogle() async {
    isLoading.value = true;
    try {
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        print("Google sign in cancelled");
        isLoading.value = false;
        return;
      }

      final GoogleSignInAuthentication googleAuth =
          await googleUser.authentication;

      final String? idToken = googleAuth.idToken;
      if (idToken == null) {
        print("ID Token is null");
        isLoading.value = false;
        return;
      }

      final response = await http.post(
        Uri.parse(
          'https://greenap-server-590591992798.asia-northeast3.run.app/login/oauth2/google',
        ),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'idToken': idToken}),
      );

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
