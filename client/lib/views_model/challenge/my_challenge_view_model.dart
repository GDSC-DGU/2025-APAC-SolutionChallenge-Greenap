import 'package:get/get.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/data/provider/challenge/my_challenge_provider.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';

class MyChallengeViewModel extends GetxController {
  final RxList<MyChallengeModel> _myChallenges = <MyChallengeModel>[].obs;
  final RxList<MyChallengeModel> _notCertificatedToday =
      <MyChallengeModel>[].obs;

  final Rx<ChallengeFilterStatus> _status = ChallengeFilterStatus.all.obs;
  final RxBool _isLoading = false.obs;

  List<MyChallengeModel> get myChallenges => _myChallenges;
  ChallengeFilterStatus get status => _status.value;
  List<MyChallengeModel> get notCertificatedToday => _notCertificatedToday;
  bool get isLoading => _isLoading.value;

  late final MyChallengeProvider _provider;
  late final ChallengeViewModel _challengeViewModel =
      Get.find<ChallengeViewModel>();

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<MyChallengeProvider>();
    loadMyChallenges();
  }

  @override
  void onReady() {
    super.onReady();
    loadMyChallenges();
  }

  void setStatus(ChallengeFilterStatus value) {
    _status.value = value;
  }

  Future<void> loadMyChallenges() async {
    if (_isLoading.value) return; // 중복 방지
    _isLoading.value = true;

    print('[DEBUG] fetchMyChallenges() 호출됨');
    try {
      final response = await _provider.getMyChallenges();
      if (response.data != null) {
        final allChallenges = _challengeViewModel.challengeList.toList();
        final challenges =
            response.data!.map((dto) {
              String? imageUrl;
              for (final category in allChallenges) {
                for (final challenge in category.challenges) {
                  if (challenge.id == dto.challengeId) {
                    imageUrl = challenge.mainImageUrl;
                    break;
                  }
                }
              }
              return dto.toModel(mainImageUrl: imageUrl);
            }).toList();
        _myChallenges.value = challenges;

        _notCertificatedToday.value =
            challenges.where((c) {
              return c.status == ChallengeStatus.running &&
                  c.isCerficatedInToday == ChallengeCertificated.FAILED;
            }).toList();
        _myChallenges.value = challenges;
      } else {
        print('[ERROR] 마이 챌린지 가져오기 실패: ${response.message}');
      }
    } catch (e) {
      print('[EXCEPTION] 마이 챌린지 조회 중 오류: $e');
    } finally {
      _isLoading.value = false;
    }
  }
}
