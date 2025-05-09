import 'package:get/get.dart';
import 'package:greenap/views_model/my_feed_list/my_feed_list_view_model.dart';

class MyFeedListBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => MyFeedListViewModel());
  }
}
