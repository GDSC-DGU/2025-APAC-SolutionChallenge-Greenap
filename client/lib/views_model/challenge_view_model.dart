import 'package:get/get.dart';

class ChallengeViewModel extends GetxController {
  var isLeftSelected = true.obs;

  void toggleView(bool leftSelected) {
    isLeftSelected.value = leftSelected;
  }
}
