import 'package:get/get.dart';
import 'package:greenap/data/provider/rank/rank_provider.dart';
import 'package:greenap/domain/models/all_ranking.dart';
import 'package:greenap/domain/models/challenge_ranking.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';
import 'package:greenap/domain/models/challenge_category.dart';

class RankingViewModel extends GetxController {
  final isLeftSelected = true.obs;
  final isLoading = false.obs;
  late final RankProvider _provider;
  final Rxn<RankingModel> allRanking = Rxn<RankingModel>();
  final Rxn<ParticipantModel> myRanking = Rxn<ParticipantModel>();
  final selectedChallengeId = Rxn<int>();
  final Rxn<ChallengeRankingModel> challengeRanking =
      Rxn<ChallengeRankingModel>();
  final RxList<ChallengeCategoryModel> availableChallenges =
      <ChallengeCategoryModel>[].obs;
  late final challengeViewModel;

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

    // 챌린지 데이터 준비 완료될 때까지 대기
    ever<bool>(challengeViewModel.isLoading, (loading) {
      if (loading == false) {
        if (isLeftSelected.value) {
          getAllRanking();
        } else {
          final firstChallenge =
              availableChallenges.isNotEmpty ? availableChallenges.first : null;

          if (firstChallenge != null) {
            selectedChallengeId.value = firstChallenge.id;
            fetchCumulativeRanking(firstChallenge.id);
          } else {
            print('[WARN] 선택 가능한 챌린지가 없습니다.');
          }
        }
      }
    });

    if (isLeftSelected.value) {
      getAllRanking();
    } else {
      final firstChallenge =
          availableChallenges.isNotEmpty ? availableChallenges.first : null;

      if (firstChallenge != null) {
        selectedChallengeId.value = firstChallenge.id;
        fetchCumulativeRanking(firstChallenge.id);
      } else {
        print('[WARN] 선택 가능한 챌린지가 없습니다.');
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
      print('[ERROR] 랭킹 정보 로딩 실패: ${response.message}');
    }
    isLoading.value = false;
  }

  Future<void> fetchCumulativeRanking(int challengeId) async {
    final response = await _provider.getChallengeRanking(challengeId);

    if (response.data != null) {
      challengeRanking.value = response.data;
    } else {
      print('[ERROR] 누적 랭킹 불러오기 실패: ${response.message}');
    }
  }
}
