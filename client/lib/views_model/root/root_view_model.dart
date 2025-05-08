import 'package:get/get.dart';

class RootViewModel extends GetxController {
  late final RxInt _selectedBottomNavigationIndex = RxInt(2);

  int get selectedIndex => _selectedBottomNavigationIndex.value;

  @override
  void onInit() async {
    super.onInit();
    final initial = Get.arguments?['initialTab'] ?? 2;
    _selectedBottomNavigationIndex.value = initial;
  }

  void changeIndex(int index) async {
    _selectedBottomNavigationIndex.value = index;
  }

  @override
  void onReady() async {}
}
