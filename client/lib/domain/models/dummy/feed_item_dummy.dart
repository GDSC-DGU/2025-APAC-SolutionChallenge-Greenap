import '../feed_item.dart';
import '../user.dart';

final List<FeedItemModel> dummyFeedItems = [
  FeedItemModel(
    category: '자원절약',
    challengeTitle: '텀블러 사용하기',
    imageUrl: 'assets/images/verification_example_image.png',
    content: '오늘은 텀블러를 들고 카페에 갔어요!',
    createdAt: DateTime.parse('2025-04-01T11:00:00'),
    user: UserModel(
      nickname: '이정선',
      profileImageUrl:
          'https://www.studiopeople.kr/common/img/default_profile.png',
    ),
  ),
  FeedItemModel(
    category: '친환경',
    challengeTitle: '비닐봉지 줄이기',
    imageUrl: 'assets/images/verification_example_image.png',
    content: '장바구니 사용 성공!',
    createdAt: DateTime.parse('2025-04-02T09:30:00'),
    user: UserModel(
      nickname: '김환경',
      profileImageUrl:
          'https://www.studiopeople.kr/common/img/default_profile.png',
    ),
  ),
];
