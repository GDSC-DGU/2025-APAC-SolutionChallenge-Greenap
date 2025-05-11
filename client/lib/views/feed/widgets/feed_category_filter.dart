import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:get/get.dart';

class FeedCategoryFilter extends StatelessWidget {
  final RxString selectedCategory;
  final List<String> categories;

  const FeedCategoryFilter({
    super.key,
    required this.selectedCategory,
    required this.categories,
  });

  @override
  Widget build(BuildContext context) {
    return Obx(
      () => Row(
        children:
            categories.map((category) {
              final isSelected = selectedCategory.value == category;
              return GestureDetector(
                onTap: () => selectedCategory.value = category,
                child: Container(
                  padding: const EdgeInsets.all(8),
                  margin: const EdgeInsets.only(right: 8),
                  decoration: BoxDecoration(
                    boxShadow: [
                      BoxShadow(
                        color: ColorSystem.black.withOpacity(0.08),
                        blurRadius: 4,
                        spreadRadius: 2,
                        offset: Offset(0, 0),
                      ),
                    ],
                    color: ColorSystem.white,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    category,
                    style: FontSystem.Button3.copyWith(
                      color:
                          isSelected
                              ? ColorSystem.gray[700]
                              : ColorSystem.gray[300],
                    ),
                  ),
                ),
              );
            }).toList(),
      ),
    );
  }
}
