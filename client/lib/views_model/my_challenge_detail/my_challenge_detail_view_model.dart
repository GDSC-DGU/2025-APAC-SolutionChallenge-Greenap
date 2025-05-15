import 'package:get/get.dart';
import 'package:greenap/data/provider/category_detail/category_detail_provider.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/data/provider/report/report_provider.dart';

class MyChallengeDetailViewModel extends GetxController {
  final Rxn<MyChallengeModel> challenge = Rxn<MyChallengeModel>();
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
    challenge.value = Get.arguments as MyChallengeModel;
    fetchChallengeDetail(challenge.value!.challengeId);
    if (challenge.value!.status == ChallengeStatus.completed) {
      fetchReport(); // 리포트용 API
    } else {
      fetchFeedList(); // 피드용 API
    }
  }

  Future<void> fetchFeedList() async {
    final response = await _feedProvider.fetchFeeds(
      null,
      'user',
      challenge.value!.id,
      null,
      null,
    );
    print('디버깅 response: ${response.data}');
    if (response.data != null) {
      feedList.assignAll(response.data!);
      print('디버깅 response: ${response.data}');
    }
  }

  Future<void> fetchReport() async {
    final response = await _reportProvider.getMyChallengeReport(
      challenge.value!.id,
    );

    if (response.data != null) {
      challengeReport = response.data;
    } else {
      print('[ERROR] 챌린지 리포트 조회 실패: ${response.message}');
    }

    update(); // GetBuilder 사용 시 UI 갱신
  }

  bool get isCompleted => challenge.value!.status == ChallengeStatus.completed;

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

  Future<void> joinChallenge(int challengeId, int durationDays) async {
    final response = await _categoryDetailProvider.joinChallenge(
      challengeId: challengeId,
      participantsDate: durationDays,
    );

    if (response.data != null) {
      print('[DEBUG]: ${response.data}');
    } else {
      Get.snackbar('도전 실패', response.message ?? '알 수 없는 오류가 발생했어요');
    }
  }
}
