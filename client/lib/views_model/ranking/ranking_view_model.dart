import 'package:get/get.dart';
import 'package:greenap/data/provider/rank/rank_provider.dart';
import 'package:greenap/domain/models/all_ranking.dart';
import 'package:greenap/domain/models/challenge_ranking.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';
import 'package:greenap/domain/models/challenge_category.dart';
import 'package:greenap/domain/models/challenge_item.dart';

class RankingViewModel extends GetxController {
  final isLeftSelected = true.obs;
  final isLoading = false.obs;
  late final RankProvider _provider;
  final Rxn<RankingModel> allRanking = Rxn<RankingModel>();
  final Rxn<ParticipantModel> myChallengeRanking = Rxn<ParticipantModel>();
  final Rxn<ParticipantModel> myRanking = Rxn<ParticipantModel>();

  final selectedChallengeId = Rxn<int>();
  final Rxn<ChallengeRankingModel> challengeRanking =
      Rxn<ChallengeRankingModel>();
  final RxList<ChallengeCategoryModel> availableChallenges =
      <ChallengeCategoryModel>[].obs;
  late final challengeViewModel;
  final RxList<ChallengeItemModel> challengeList = <ChallengeItemModel>[].obs;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<RankProvider>();
    challengeViewModel = Get.find<ChallengeViewModel>();
    final argument = Get.arguments;

    if (argument is ParticipantModel) {
      myRanking.value = ParticipantModel(
        rank: argument.rank,
        user: ParticipantUserModel(
          nickname: argument.user.nickname,
          profileImageUrl: argument.user.profileImageUrl,
          longestConsecutiveParticipationCount:
              argument.user.longestConsecutiveParticipationCount,
        ),
      );
    }

    // 🔥 챌린지 리스트 연결
    availableChallenges.value = challengeViewModel.challengeList.toList();
    challengeList.value =
        availableChallenges
            .expand((category) => category.challenges) // 모든 챌린지를 하나의 리스트로 평탄화
            .toList();

    ever(isLeftSelected, (bool leftSelected) {
      if (leftSelected) {
        getAllRanking();
      } else {
        if (availableChallenges.isNotEmpty) {
          selectedChallengeId.value = availableChallenges.first.id;
          fetchCumulativeRanking(availableChallenges.first.id);
        }
      }
    });

    // 최초 상태 처리
    if (isLeftSelected.value) {
      getAllRanking();
    } else {
      if (availableChallenges.isNotEmpty) {
        selectedChallengeId.value = availableChallenges.first.id;
        fetchCumulativeRanking(availableChallenges.first.id);
      }
    }
  }

  void toggleView(bool leftSelected) {
    isLeftSelected.value = leftSelected;
  }

  Future<void> getAllRanking() async {
    isLoading.value = true;
    final response = await _provider.getAllRanking();

    if (response.data != null) {
      allRanking.value = response.data;
    } else {
      allRanking.value = null;
      print('[ERROR] 랭킹 정보 로딩 실패: ${response.message}');
    }
    isLoading.value = false;
  }

  Future<void> fetchCumulativeRanking(int challengeId) async {
    final allChallengeResponse = await _provider.getChallengeRanking(
      challengeId,
    );
    final userChallengeResponse = await _provider.getUserChallengeRanking(
      challengeId,
    );

    if (allChallengeResponse.data != null) {
      challengeRanking.value = allChallengeResponse.data;
      print('[ERROR] 누적 랭킹 불러오기 성공: ${challengeRanking.value}');
    } else {
      challengeRanking.value = null;
      print('누적 챌린지 랭킹이 없습니다.');
    }
    if (userChallengeResponse.data != null) {
      myChallengeRanking.value = ParticipantModel(
        rank: userChallengeResponse.data!.userRankInfo.rank,
        user: ParticipantUserModel(
          nickname: userChallengeResponse.data!.userRankInfo.user.nickname,
          profileImageUrl:
              userChallengeResponse.data!.userRankInfo.user.profileImageUrl,
          totalParticipationCount:
              userChallengeResponse
                  .data!
                  .userRankInfo
                  .user
                  .totalParticipationCount,
        ),
      );
      print('[ERROR] 누적 랭킹 불러오기 성공: ${myChallengeRanking.value}');
    } else {
      myChallengeRanking.value = null;
      print('아직 챌린지를 참여하지 않았습니다.');
    }
  }
}
