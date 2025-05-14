import 'package:get/get.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:greenap/data/provider/feed/feed_provider.dart';
import 'package:greenap/views_model/challenge/challenge_view_model.dart';

class MyFeedListViewModel extends GetxController {
  final RxList<FeedItemModel> _feedList = <FeedItemModel>[].obs;
  final RxBool _isLoading = false.obs;
  final RxString selectedCategory = '자원 절약'.obs;
  final RxList<FeedItemModel> filteredList = <FeedItemModel>[].obs;

  List<FeedItemModel> get feedList => _feedList;
  bool get isLoading => _isLoading.value;

  late final FeedProvider _provider;
  late final ChallengeViewModel _challengeViewModel;

  @override
  void onInit() {
    super.onInit();
    _provider = Get.find<FeedProvider>();
    _challengeViewModel = Get.find<ChallengeViewModel>();
    ever(selectedCategory, (_) => fetchFeedList());
  }

  int? get selectedCategoryId {
    final categoryName = selectedCategory.value;
    final match = _challengeViewModel.challengeList.firstWhereOrNull(
      (cat) => cat.title == categoryName,
    );
    return match?.id;
  }

  Future<void> fetchFeedList() async {
    _isLoading.value = true;
    try {
      final categoryId = selectedCategoryId;
      final response = await _provider.fetchFeeds(
        categoryId,
        "user",
        null,
        null,
        null,
      );
      print("디버깅: ${response.data}");
      if (response.data != null) {
        _feedList.assignAll(response.data!);
      } else {
        _feedList.clear();
      }
      print('[DEBUG] response: , ${response.data}');
    } catch (e) {
      print('[EXCEPTION] 피드 조회 중 오류: $e');
    } finally {
      _isLoading.value = false;
    }
  }
}
