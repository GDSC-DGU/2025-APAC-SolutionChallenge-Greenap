import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/data/dto/challenge_report_dto.dart';

class ReportProvider extends BaseConnect {
  Future<ResponseWrapper<ChallengeReportModel>> getMyChallengeReport(
    int uderChallengeId,
  ) async {
    print('[DEBUG] getMyChallenges() 실행됨');
    final response = await getRequest(
      '/api/v1/challenges/user/$uderChallengeId/report?today_date=2025-05-19',
    );

    print('[DEBUG] 전체 응답 body: ${response.body}');
    if (response.statusCode == 200) {
      final body = response.body;
      return ResponseWrapper(
        code: body['code'],
        data: ChallengeReportDto.fromJson(body['data']).toModel(),
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
