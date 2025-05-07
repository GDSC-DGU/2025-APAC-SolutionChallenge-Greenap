import 'package:get/get.dart';
import 'package:greenap/domain/enums/challenge.dart';

class MyChallengeViewModel extends GetxController {
  final status = ChallengeFilterStatus.all.obs;

  void setStatus(ChallengeFilterStatus value) {
    status.value = value;
  }
}
