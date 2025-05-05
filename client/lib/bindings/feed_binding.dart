import 'package:get/get.dart';
import 'package:greenap/views_model/feed_view_model.dart';

class FeedBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<FeedViewModel>(() => FeedViewModel());
  }
}
