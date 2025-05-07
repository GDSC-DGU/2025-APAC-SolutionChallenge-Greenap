import 'package:get/get.dart';
import 'package:greenap/views_model/category_detail/category_detail_view_model.dart';

class CategoryDetailBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => CategoryDetailViewModel());
  }
}
