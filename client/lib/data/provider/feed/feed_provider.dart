import 'package:greenap/core/network/base_connect.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/data/dto/feed_dto.dart';

class FeedProvider extends BaseConnect {
  Future<ResponseWrapper<List<FeedItemModel>>> fetchFeeds(
    int? categoryId,
    String? scope,
    int? userChallengeId,
    int? page,
    int? size,
  ) async {
    final query = <String, String>{
      if (categoryId != null) 'category_id': categoryId.toString(),
      if (scope != null) 'scope': scope,
      if (userChallengeId != null)
        'user_challenge_id': userChallengeId.toString(),
      if (page != null) 'page': page.toString(),
      if (size != null) 'size': size.toString(),
    };
    print('[DEBUG] query: $query');
    final response = await getRequest('/api/v1/feeds', query: query);

    print('[DEBUG] 피드 조회 응답: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body;
      final rawFeeds = body['data']['feedList'];
      final List<FeedItemModel> parsedFeeds =
          rawFeeds
              .map<FeedItemModel>((e) => FeedItemDto.fromJson(e).toModel())
              .toList();

      return ResponseWrapper(
        code: body['code'],
        data: parsedFeeds,
        message: body['message'],
      );
    }

    return ResponseWrapper(
      code: response.body['code'] ?? '500',
      data: null,
      message: response.body['error']?['message'] ?? '알 수 없는 오류',
    );
  }

  Future<ResponseWrapper<int?>> postFeed(
    int userChallengeId,
    String imageUrl,
    String content,
    String publishDate,
  ) async {
    final response = await postRequest('/api/v1/feeds', {
      'user_challenge_id': userChallengeId,
      'image_url': imageUrl,
      'content': content,
      'publish_date': publishDate,
    });

    print('[DEBUG] 피드 조회 응답: ${response.body}');

    if (response.statusCode == 200) {
      final body = response.body;
      return ResponseWrapper(
        code: body['code'],
        data: body['data']['feed_id'],
        message: body['message'],
      );
    }

    return ResponseWrapper(
      code: response.body['code'] ?? '500',
      data: null,
      message: response.body['error']?['message'] ?? '알 수 없는 오류',
    );
  }
}
