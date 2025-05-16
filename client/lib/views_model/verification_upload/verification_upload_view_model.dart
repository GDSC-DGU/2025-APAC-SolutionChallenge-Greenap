import 'dart:io';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:greenap/data/provider/verification/verification_provider.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:greenap/domain/models/my_challenge.dart';

class VerificationUploadViewModel extends GetxController {
  final int challengeId;
  final int userChallengeId;
  final MyChallengeModel myChallengeModel;

  VerificationUploadViewModel({
    required this.challengeId,
    required this.userChallengeId,
    required this.myChallengeModel,
  });

  final RxBool isChecked = false.obs;
  final Rx<File?> selectedImage = Rx<File?>(null);
  final RxBool isLoading = false.obs;
  final RxnString uploadedImageUrl = RxnString();
  final RxBool isFinished = false.obs;

  final challengeDetail = Rxn<ChallengeDetailModel>();
  late final VerificationProvider _provider;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<VerificationProvider>();

    fetchChallengeDetail();
  }

  Future<void> fetchChallengeDetail() async {
    print('[DEBUG] 챌린지 상세 정보 요청 시작: $challengeId');
    final response = await _provider.getChallengeDetail(challengeId);
    print('[DEBUG] 응답 수신: status=${response.code}, message=${response.data}');

    if (response.data != null) {
      challengeDetail.value = response.data;
    } else {
      print('[ERROR] 챌린지 상세 정보 없음');
    }
  }

  /// 이미지 선택
  Future<void> pickImage() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);
    if (pickedFile != null) {
      selectedImage.value = File(pickedFile.path);
    }
  }

  /// 인증 요청
  Future<String?> submitVerification() async {
    if (selectedImage.value == null) return '이미지를 선택해주세요.';

    isLoading.value = true;
    try {
      final response = await _provider.uploadVerificationImage(
        userChallengeId: userChallengeId,
        imageFile: selectedImage.value!,
      );
      if (response.message != 'failure') {
        uploadedImageUrl.value = response.data['image_url'];
        isFinished.value = response.data['is_finished'];
        print("is_finished : ${isFinished.value}");
        print("image_url : ${uploadedImageUrl.value}");
      }
      return response.message;
    } catch (e) {
      return '인증 요청 중 오류가 발생했습니다.';
    } finally {
      isLoading.value = false;
    }
  }
}
