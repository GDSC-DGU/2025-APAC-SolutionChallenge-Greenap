import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/Font_system.dart';

enum CategorySize { large, medium, small }

class ChallengeCategoryItem extends StatelessWidget {
  final CategorySize size;
  final Map<String, dynamic> category;

  ChallengeCategoryItem({
    super.key,
    required this.category,
    required this.size,
  });

  @override
  Widget build(BuildContext context) {
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
        gradient: category['color'],
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
            category['name'],
            style: FontSystem.Body1Bold.copyWith(color: ColorSystem.gray[800]),
          ),
          Expanded(child: Image.asset(category['image'], fit: BoxFit.contain)),
        ],
      ),
    );
  }

  Widget _buildMediumLayout() {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 20, vertical: 14),
      decoration: BoxDecoration(
        gradient: category['color'],
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
            category['name'],
            style: FontSystem.Body1Bold.copyWith(color: ColorSystem.gray[800]),
          ),

          Expanded(child: Image.asset(category['image'], fit: BoxFit.contain)),
        ],
      ),
    );
  }

  Widget _buildLargeLayout() {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 20, vertical: 16),
      decoration: BoxDecoration(
        gradient: category['color'],
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
              category['name'],
              style: FontSystem.Body1Bold.copyWith(
                color: ColorSystem.gray[800],
              ),
            ),
          ),
          Expanded(child: Image.asset(category['image'], fit: BoxFit.contain)),
        ],
      ),
    );
  }
}
