import 'package:get/get.dart';

class RootViewModel extends GetxController {
  late final RxInt _selectedBottomNavigationIndex = RxInt(2);

  int get selectedIndex => _selectedBottomNavigationIndex.value;

  @override
  void onInit() async {
    super.onInit();

    _selectedBottomNavigationIndex.value = 2;
  }

  void changeIndex(int index) async {
    _selectedBottomNavigationIndex.value = index;
  }

  @override
  void onReady() async {}
}
