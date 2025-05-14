import 'package:get/get.dart';
import 'package:greenap/views_model/feed_post/feed_post_view_model.dart';

class FeedPostBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => FeedPostViewModel());
  }
}
