import 'package:get/get.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';

class FeedViewModel extends GetxController {
  final RxList<FeedItemModel> _feedList = <FeedItemModel>[].obs;
  final RxBool _isLoading = false.obs;
  final RxString selectedCategory = '전체'.obs;
  final RxList<FeedItemModel> filteredList = <FeedItemModel>[].obs;

  List<FeedItemModel> get feedList => _feedList;
  bool get isLoading => _isLoading.value;

  late final FeedProvider _provider;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<FeedProvider>();

    fetchFeedList();

    ever(selectedCategory, (_) => filterFeedList());
  }

  void filterFeedList() {
    if (selectedCategory.value == '전체') {
      filteredList.assignAll(_feedList);
    } else {
      filteredList.assignAll(
        _feedList.where((item) => item.category == selectedCategory.value),
      );
    }
  }

  Future<void> fetchFeedList() async {
    _isLoading.value = true;
    try {
      final response = await _provider.fetchFeeds(null, null, null, null, null);
      if (response.data != null) {
        _feedList.assignAll(response.data!);
        filteredList.assignAll(_feedList);
        print('[DEBUG] response: , $_feedList');
      } else {
        print('[WARN] 피드 데이터가 없습니다.');
      }
    } catch (e) {
      print('[EXCEPTION] 피드 조회 중 오류: $e');
    } finally {
      _isLoading.value = false;
    }
  }
}
