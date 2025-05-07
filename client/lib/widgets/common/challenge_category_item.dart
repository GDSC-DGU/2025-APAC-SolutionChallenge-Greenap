import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/Font_system.dart';
import 'package:greenap/models/challenge_category.dart';
import 'package:get/get.dart';

enum CategorySize { large, medium, small }

class ChallengeCategoryItem extends StatelessWidget {
  final CategorySize size;
  final ChallengeCategoryModel category;
  final Gradient backgroundGradient;

  ChallengeCategoryItem({
    super.key,
    required this.category,
    required this.size,
    required this.backgroundGradient,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => Get.toNamed('/category', arguments: category),
      child: _buildLayoutBySize(),
    );
  }

  Widget _buildLayoutBySize() {
    switch (size) {
      case CategorySize.large:
        return _buildLargeLayout();
      case CategorySize.medium:
        return _buildMediumLayout();
      case CategorySize.small:
        return _buildSmallLayout();
    }
  }

  Widget _buildSmallLayout() {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 0, vertical: 12),
      decoration: BoxDecoration(
        gradient: backgroundGradient,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.04),
            spreadRadius: 2,
            blurRadius: 4,
            offset: Offset(0, 0),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Text(
            category.title,
            style: FontSystem.Body1Bold.copyWith(color: ColorSystem.gray[800]),
          ),
          Expanded(child: Image.asset(category.imageUrl, fit: BoxFit.contain)),
        ],
      ),
    );
  }

  Widget _buildMediumLayout() {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 20, vertical: 14),
      decoration: BoxDecoration(
        gradient: backgroundGradient,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.04),
            spreadRadius: 2,
            blurRadius: 4,
            offset: Offset(0, 0),
          ),
        ],
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,

        children: [
          Text(
            category.title,
            style: FontSystem.Body1Bold.copyWith(color: ColorSystem.gray[800]),
          ),

          Expanded(child: Image.asset(category.imageUrl, fit: BoxFit.contain)),
        ],
      ),
    );
  }

  Widget _buildLargeLayout() {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 20, vertical: 16),
      decoration: BoxDecoration(
        gradient: backgroundGradient,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.04),
            spreadRadius: 2,
            blurRadius: 4,
            offset: Offset(0, 0),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Align(
            alignment: Alignment.centerLeft,
            child: Text(
              category.title,
              style: FontSystem.Body1Bold.copyWith(
                color: ColorSystem.gray[800],
              ),
            ),
          ),
          Expanded(child: Image.asset(category.imageUrl, fit: BoxFit.contain)),
        ],
      ),
    );
  }
}
