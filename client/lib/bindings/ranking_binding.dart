import 'package:get/get.dart';
import 'package:greenap/views_model/ranking/ranking_view_model.dart';

class RankingBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => RankingViewModel());
  }
}
