import 'package:get/get.dart';

class RootViewModel extends GetxController {
  final RxInt selectedIndex = 2.obs;

  void changeIndex(int index) {
    selectedIndex.value = index;
  }
}
