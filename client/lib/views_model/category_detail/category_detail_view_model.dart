import 'package:get/get.dart';
import 'package:greenap/models/challenge_category.dart';

class CategoryDetailViewModel extends GetxController {
  late final ChallengeCategoryModel category;

  @override
  void onInit() {
    super.onInit();

    category = Get.arguments as ChallengeCategoryModel;
  }
}
