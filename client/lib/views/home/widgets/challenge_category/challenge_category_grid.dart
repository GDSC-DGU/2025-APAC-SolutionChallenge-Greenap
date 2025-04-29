import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import './challenge_category_item.dart';

class ChallengeCategoryGrid extends StatelessWidget {
  const ChallengeCategoryGrid({super.key});

  @override
  Widget build(BuildContext context) {
    final categories = [
      {
        'name': '자원절약',
        'image': 'assets/images/resource.png',
        'color': ColorSystem.pinkGradient,
      },
      {
        'name': '친환경',
        'image': 'assets/images/eco.png',
        'color': ColorSystem.blueGradient,
      },
      {
        'name': '교통절감',
        'image': 'assets/images/transport.png',
        'color': ColorSystem.yellowGradient,
      },
      {
        'name': '재사용',
        'image': 'assets/images/reuse.png',
        'color': ColorSystem.greenGradient,
      },
    ];
    return Container(
      child: SizedBox(
        height: 200,
        child: Row(
          children: [
            // 왼쪽 큰 카테고리
            Expanded(
              flex: 3,
              child: ChallengeCategoryItem(
                category: categories[0],
                size: CategorySize.large,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              flex: 4,
              child: Column(
                children: [
                  // 중간 카테고리 (위 절반)
                  Expanded(
                    flex: 2,
                    child: SizedBox.expand(
                      child: ChallengeCategoryItem(
                        category: categories[1],
                        size: CategorySize.medium,
                      ),
                    ),
                  ),
                  const SizedBox(height: 12),
                  // 작은 카테고리 2개 (아래 절반)
                  Expanded(
                    flex: 2,
                    child: Row(
                      children: [
                        Expanded(
                          child: ChallengeCategoryItem(
                            category: categories[2],
                            size: CategorySize.small,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: ChallengeCategoryItem(
                            category: categories[3],
                            size: CategorySize.small,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
