import 'package:get/get.dart';
import 'package:greenap/data/provider/category_detail/category_detail_provider.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/domain/models/dummy/challenge_report_dummy.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';
import 'package:greenap/domain/models/challenge_detail.dart';

class MyChallengeDetailViewModel extends GetxController {
  late final MyChallengeModel challenge;
  final challengeDetail = Rxn<ChallengeDetailModel>();
  final RxList<FeedItemModel> feedList = <FeedItemModel>[].obs;
  ChallengeReportModel? challengeReport;
  late final FeedProvider _feedProvider;
  late final CategoryDetailProvider _categoryDetailProvider;

  @override
  void onInit() {
    super.onInit();
    _feedProvider = Get.find<FeedProvider>();
    _categoryDetailProvider = Get.find<CategoryDetailProvider>();
    challenge = Get.arguments as MyChallengeModel;
    // fetchChallengeDetail(challenge.challengeId);
    if (challenge.status == ChallengeStatus.completed) {
      fetchReport(); // 리포트용 API
    } else {
      fetchFeedList(); // 피드용 API
    }
  }

  Future<void> fetchFeedList() async {
    final response = await _feedProvider.fetchFeeds(
      null,
      'user',
      challenge.id,
      null,
      null,
    );

    if (response.data != null) {
      feedList.assignAll(response.data!);
    }
  }

  void fetchReport() async {
    // 실제 API 호출로 교체 예정
    challengeReport = dummyChallengeReport;
    update(); // GetBuilder 사용 시 필요
  }

  bool get isCompleted => challenge.status == ChallengeStatus.completed;

  Future<void> fetchChallengeDetail(int challengeId) async {
    final response = await _categoryDetailProvider.getChallengeDetail(
      challengeId,
    );

    if (response.data != null) {
      challengeDetail.value = response.data;
    } else {
      print('[ERROR] 챌린지 상세 정보 로딩 실패: ${response.message}');
    }
  }
}
