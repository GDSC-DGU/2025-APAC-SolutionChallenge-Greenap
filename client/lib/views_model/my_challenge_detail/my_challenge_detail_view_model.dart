import 'package:get/get.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/domain/models/dummy/challenge_report_dummy.dart';
import 'package:greenap/domain/models/dummy/challenge_detail_dummy.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';

import 'package:greenap/domain/models/challenge_detail.dart';

class MyChallengeDetailViewModel extends GetxController {
  late final MyChallengeModel challenge;

  final RxList<FeedItemModel> feedList = <FeedItemModel>[].obs;
  ChallengeReportModel? challengeReport;
  final FeedProvider _feedProvider = Get.find<FeedProvider>();

  @override
  void onInit() {
    super.onInit();
    challenge = Get.arguments as MyChallengeModel;
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

  Future<ChallengeDetailModel?> fetchChallengeDetail(int challengeId) async {
    final result = dummyChallengeDetails.firstWhereOrNull(
      (challenge) => challenge.id == challengeId,
    );

    if (result == null) {
      print("해당 ID의 챌린지를 찾을 수 없습니다.");
    }

    return result;
  }
}
