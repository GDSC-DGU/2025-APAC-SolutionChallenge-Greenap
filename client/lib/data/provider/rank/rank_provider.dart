import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/data/dto/encourage_dto.dart';
import 'package:greenap/domain/models/my_rank.dart';

class RankProvider extends BaseConnect {
  Future<ResponseWrapper<MyRankModel>> getUserRanking() async {
    print('[DEBUG] getUserRanking() 실행됨');
    final response = await getRequest('/api/v1/ranks/user');

    print('[DEBUG] 전체 응답 body: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body;
      final data = body['data']['user_rank_info'];

      final myRank = MyRankModel(
        rank: data['rank'] ?? 0,
        longestConsecutiveParticipationCount:
            data['longestConsecutiveParticipationCount'] ?? 0,
      );

      return ResponseWrapper<MyRankModel>(
        code: body['code'],
        data: myRank,
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
