import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/domain/models/all_ranking.dart';
import 'package:greenap/domain/models/challenge_ranking.dart';
import 'package:greenap/data/dto/all_ranking_dto.dart';
import 'package:greenap/data/dto/challenge_ranking_dto.dart';
import 'package:greenap/data/dto/my_rank_dto.dart';

class RankProvider extends BaseConnect {
  Future<ResponseWrapper<RankingModel>> getAllRanking() async {
    final response = await getRequest('/api/v1/ranks');

    print('[DEBUG] 전체 응답 body: ${response.body}');
    print('[DEBUG] 전체 응답 statusCode: ${response.statusCode}');
    if (response.statusCode == 200) {
      final body = response.body;
      if (body != null) {
        final data = AllRankingDto.fromJson(body['data']).toModel();
        return ResponseWrapper(
          code: body['code'],
          message: body['message'],
          data: data,
        );
      } else {
        return ResponseWrapper(
          code: 'NO_DATA',
          message: 'No ranking data found.',
          data: null,
        );
      }
    }

    return ResponseWrapper(
      code: 'ERROR',
      message: response.body['message'] ?? 'Unknown error',
      data: null,
    );
  }

  Future<ResponseWrapper<ParticipantModel>> getUserRanking() async {
    print('[DEBUG] getUserRanking() 실행됨');
    final response = await getRequest('/api/v1/ranks/user');

    print('[DEBUG] 전체 응답 body: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body;
      if (body != null) {
        final dto = MyRankDto.fromJson(body['data']['user_rank_info']);
        final myRank = dto.toModel();
        return ResponseWrapper<ParticipantModel>(
          code: body['code'],
          data: myRank,
          message: body['message'],
        );
      } else {
        return ResponseWrapper(
          code: 'NO_DATA',
          message: 'No ranking data found.',
          data: null,
        );
      }
    }

    return ResponseWrapper(
      code: "ERROR",
      data: null,
      message: response.body['message'] ?? 'Unknown error',
    );
  }

  Future<ResponseWrapper<ChallengeRankingModel>> getChallengeRanking(
    int challengeId,
  ) async {
    final response = await getRequest('/api/v1/ranks/$challengeId');
    print('[DEBUG] 전체 응답 body: ${response.body}');

    if (response.statusCode == 200) {
      final dto = ChallengeRankingDto.fromJson(response.body['data']);
      return ResponseWrapper(
        code: response.body['code'],
        message: response.body['message'],
        data: dto.toModel(), // 모델 매핑 구현 필요
      );
    }
    return ResponseWrapper(code: 'ERROR', message: '불러오기 실패', data: null);
  }

  Future<ResponseWrapper<MyChallengeRankingModel>> getUserChallengeRanking(
    int challengeId,
  ) async {
    final response = await getRequest('/api/v1/ranks/user/$challengeId');

    if (response.statusCode == 200) {
      final dto = MyChallengeRankingDto.fromJson(response.body['data']);
      return ResponseWrapper(
        code: response.body['code'],
        message: response.body['message'],
        data: dto.toModel(),
      );
    }

    return ResponseWrapper(code: 'ERROR', message: '불러오기 실패', data: null);
  }
}
