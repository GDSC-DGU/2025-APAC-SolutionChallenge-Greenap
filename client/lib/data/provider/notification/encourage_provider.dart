import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/data/dto/encourage_dto.dart';

class EncourageProvider extends BaseConnect {
  Future<ResponseWrapper<String>> getEncourageMessage() async {
    print('[DEBUG] getChallengeCategories() 실행됨');
    final response = await getRequest('/api/v1/notification/encourage');

    print('[DEBUG] 전체 응답 body: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body;
      return ResponseWrapper<String>(
        code: body['code'],
        data: body['data']['message'],
        message: body['message'],
      );
    }

    return ResponseWrapper(
      code: response.body['code'] ?? '500',
      data: null,
      message: response.body['error']?['message'],
    );
  }
}
