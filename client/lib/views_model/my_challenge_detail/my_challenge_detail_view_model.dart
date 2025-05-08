import 'package:get/get.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/domain/models/dummy/feed_item_dummy.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/domain/models/dummy/challenge_report_dummy.dart';

class MyChallengeDetailViewModel extends GetxController {
  late final MyChallengeModel challenge;

  final RxList<FeedItemModel> feedList = <FeedItemModel>[].obs;
  ChallengeReportModel? challengeReport;

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

  void fetchFeedList() async {
    // 실제 챌린지 인증 피드 리스트 불러오기로 교체 필요
    final List<FeedItemModel> fetched = dummyFeedItems;
    feedList.assignAll(fetched);
  }

  void fetchReport() async {
    // 실제 API 호출로 교체 예정
    challengeReport = dummyChallengeReport;
    update(); // GetBuilder 사용 시 필요
  }

  bool get isCompleted => challenge.status == ChallengeStatus.completed;
}
