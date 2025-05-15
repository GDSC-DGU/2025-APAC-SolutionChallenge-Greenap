import 'dart:io';
import 'package:get/get.dart';
import 'package:get/get_connect/connect.dart';
import 'package:get/get_connect/http/src/multipart/form_data.dart';
import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/data/dto/challege_detail_dto.dart';

class VerificationProvider extends BaseConnect {
  Future<ResponseWrapper<dynamic>> uploadVerificationImage({
    required int userChallengeId,
    required File imageFile,
  }) async {
    final form = FormData({
      'image': MultipartFile(
        imageFile,
        filename: imageFile.path.split('/').last,
      ),
    });

    print(
      '[DEBUG] 요청 url : /api/v1/challenges/user/$userChallengeId/certification',
    );

    final Response response = await post(
      '/api/v1/challenges/user/$userChallengeId/certification',
      form,

      contentType: 'multipart/form-data',
    );

    print('[DEBUG] 전체 응답 body: ${response.body}');

    if (response.statusCode == 200) {
      print('[DEBUG] 전체 응답 body: ${response.body}');

      final body = response.body;
      return ResponseWrapper(
        code: body['code'],
        data: body['data'],
        message: body['message'],
      );
    } else {
      return ResponseWrapper(
        code: response.body['code'] ?? '500',
        data: null,
        message: response.body['message'] ?? '알 수 없는 오류',
      );
    }
  }

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

  Future<ResponseWrapper<String>> postIce(int userChallengeId) async {
    print('[DEBUG] postIce() 실행됨');

    final response = await postRequest(
      '/api/v1/challenges/user/$userChallengeId/ice',
      {},
    );
    print('[DEBUG] 전체 응답 body: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body as Map<String, dynamic>;
      return ResponseWrapper<String>(
        code: body['code'] ?? '',
        data: body['data']?.toString(),
        message: body['message'] ?? '',
      );
    }

    return ResponseWrapper(
      code: response.body['code'] ?? '500',
      data: null,
      message: response.body['error']?['message'] ?? '데이터 없음',
    );
  }
}
