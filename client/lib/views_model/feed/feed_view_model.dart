import 'package:get/get.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/domain/models/dummy/feed_item_dummy.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';

class FeedViewModel extends GetxController {
  final RxList<FeedItemModel> _feedList = <FeedItemModel>[].obs;
  final RxBool _isLoading = false.obs;

  List<FeedItemModel> get feedList => _feedList;
  bool get isLoading => _isLoading.value;

  late final FeedProvider _provider;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<FeedProvider>();
    fetchFeedList();
  }

  Future<void> fetchFeedList() async {
    _isLoading.value = true;
    try {
      final response = await _provider.fetchFeeds(null, null, null, null, null);
      print('[DEBUG] response: , ${response.data}');
    } catch (e) {
      print('[EXCEPTION] 피드 조회 중 오류: $e');
    } finally {
      _isLoading.value = false;
    }
  }
}
