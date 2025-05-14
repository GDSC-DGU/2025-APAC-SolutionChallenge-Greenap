import 'package:get/get.dart';
import 'package:greenap/data/provider/category_detail/category_detail_provider.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/core/network/response_wrapper.dart';
import 'package:greenap/domain/models/dummy/challenge_report_dummy.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/data/provider/report/report_provider.dart';

class MyChallengeDetailViewModel extends GetxController {
  late final MyChallengeModel challenge;
  final challengeDetail = Rxn<ChallengeDetailModel>();
  final RxList<FeedItemModel> feedList = <FeedItemModel>[].obs;
  ChallengeReportModel? challengeReport;
  late final FeedProvider _feedProvider;
  late final CategoryDetailProvider _categoryDetailProvider;
  late final ReportProvider _reportProvider;

  @override
  void onInit() {
    super.onInit();
    _feedProvider = Get.find<FeedProvider>();
    _reportProvider = Get.find<ReportProvider>();
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
    print('디버깅 response: ${response.data}');
    if (response.data != null) {
      feedList.assignAll(response.data!);
      print('디버깅2 response: ${response.data}');
    }
  }

  Future<void> fetchReport() async {
    final response = await _reportProvider.getMyChallengeReport(challenge.id);

    if (response.data != null) {
      challengeReport = response.data;
    } else {
      print('[ERROR] 챌린지 리포트 조회 실패: ${response.message}');
    }

    update(); // GetBuilder 사용 시 UI 갱신
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
