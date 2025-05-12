import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/data/dto/user_info_dto.dart';

class AuthProvider extends BaseConnect {
  Future<ResponseWrapper<UserInfoDto>> googleLogin(
    String accessToken,
    String idToken,
  ) async {
    final response = await post('/api/v1/auth/oauth2/token', {
      'registrationId': 'google',
      'accessToken': accessToken,
      'idToken': idToken,
    });

    if (response.statusCode == 200 && response.body is Map<String, dynamic>) {
      final body = response.body as Map<String, dynamic>;
      return ResponseWrapper<UserInfoDto>(
        code: body['code'],
        data: UserInfoDto.fromJson(body['data']),
        message: body['message'],
      );
    }

    final body = response.body as Map<String, dynamic>;
    return ResponseWrapper<UserInfoDto>(
      code: body['code'],
      data: null,
      message: body['message'],
    );
  }
}
