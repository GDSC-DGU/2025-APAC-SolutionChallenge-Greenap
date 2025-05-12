import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/data/dto/challenge_category_dto.dart';

class ChallengeProvider extends BaseConnect {
  Future<ResponseWrapper<List<ChallengeCategoryDto>>>
  getChallengeCategories() async {
    print('[DEBUG] getChallengeCategories() 실행됨');
    final response = await getRequest('/api/v1/challenges');

    print('[DEBUG] 전체 응답 body: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body;
      final List<dynamic> rawCategories = body['data']['categories'];

      return ResponseWrapper<List<ChallengeCategoryDto>>(
        code: body['code'],
        data:
            rawCategories.map((e) => ChallengeCategoryDto.fromJson(e)).toList(),
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
