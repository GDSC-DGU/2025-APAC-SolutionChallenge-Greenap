import 'package:get/get.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/domain/models/user.dart';
import 'package:greenap/domain/models/dummy/feed_item_dummy.dart';

class MyFeedListViewModel extends GetxController {
  late final int categoryId; // 피드 조회시 필요
  final feedList = <FeedItemModel>[].obs;
  final isLoading = false.obs;

  @override
  void onInit() {
    super.onInit();
    categoryId = Get.arguments as int;
    fetchFeedList();
  }

  Future<void> fetchFeedList() async {
    isLoading.value = true;
    try {
      await Future.delayed(Duration(milliseconds: 500));
      feedList.value = dummyFeedItems;
    } finally {
      isLoading.value = false;
    }
  }
}
