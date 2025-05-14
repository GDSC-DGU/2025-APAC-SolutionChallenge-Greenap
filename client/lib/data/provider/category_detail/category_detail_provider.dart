import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/data/dto/challege_detail_dto.dart';

class CategoryDetailProvider extends BaseConnect {
  Future<ResponseWrapper<ChallengeDetailModel>> getChallengeDetail(
    int challengeId,
  ) async {
    print('[DEBUG] getChallengeDetail() 실행됨');

    final response = await getRequest('/api/v1/challenges/$challengeId');
    print('[DEBUG] 전체 응답 body: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body;
      final challenge = body['data'];

      return ResponseWrapper<ChallengeDetailModel>(
        code: body['code'],
        data: ChallengeDetailDto.fromJson(challenge).toModel(),
        message: body['message'],
      );
    }

    return ResponseWrapper(
      code: response.body['code'] ?? '500',
      data: null,
      message: response.body['error']?['message'] ?? '데이터 없음',
    );
  }

  Future<ResponseWrapper<dynamic>> joinChallenge({
    required int challengeId,
    required int participantsDate,
  }) async {
    final today = DateTime.now();
    final formattedDate =
        "${today.year.toString().padLeft(4, '0')}-${today.month.toString().padLeft(2, '0')}-${today.day.toString().padLeft(2, '0')}";

    final response = await postRequest('/api/v1/challenges', {
      'challenge_id': challengeId,
      'participants_date': participantsDate,
      'participants_start_date': formattedDate,
    });

    print('[DEBUG] 챌린지 참여 요청 결과: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body;
      final data = body['data'];

      return ResponseWrapper<int>(
        code: body['code'],
        data: data?['userChallengeId'],
        message: body['message'],
      );
    } else {
      return ResponseWrapper(
        code: response.body['code'] ?? '500',
        data: null,
        message: response.body['message'] ?? '참여에 실패하였습니다',
      );
    }
  }
}
