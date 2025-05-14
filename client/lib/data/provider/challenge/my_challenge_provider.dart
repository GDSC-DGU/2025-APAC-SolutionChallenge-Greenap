import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/data/dto/my_challenge_dto.dart';

class MyChallengeProvider extends BaseConnect {
  Future<ResponseWrapper<List<MyChallengeDto>>> getMyChallenges() async {
    print('[DEBUG] getMyChallenges() 실행됨');
    final response = await getRequest('/api/v1/challenges/user?search_date');

    print('[DEBUG] 전체 응답 body: ${response.body}');
    if (response.statusCode == 200) {
      final body = response.body;
      final List<dynamic> rawChallenges = body['data']['user_challenges'];

      return ResponseWrapper(
        code: body['code'],
        data: rawChallenges.map((e) => MyChallengeDto.fromJson(e)).toList(),
        message: body['message'],
      );
    }

    return ResponseWrapper(
      code: response.body['code'] ?? '500',
      data: null,
      message: response.body['error']?['message'] ?? 'Unknown error',
    );
  }
}
