import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:get/get.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/views_model/challenge/my_challenge_view_model.dart';

class CategoryFilter extends GetView<MyChallengeViewModel> {
  CategoryFilter({super.key}) {
    Get.put(MyChallengeViewModel());
  }

  @override
  Widget build(BuildContext context) {
    return Obx(
      () => Row(
        mainAxisAlignment: MainAxisAlignment.start,
        children:
            ChallengeFilterStatus.values.map((status) {
              final isSelected = controller.status == status;
              return GestureDetector(
                onTap: () => controller.setStatus(status),
                child: Container(
                  padding: const EdgeInsets.all(8),
                  margin: EdgeInsets.only(right: 8),
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
                    _label(status),
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

  String _label(ChallengeFilterStatus status) {
    switch (status) {
      case ChallengeFilterStatus.all:
        return '전체';
      case ChallengeFilterStatus.running:
        return '진행중';
      case ChallengeFilterStatus.completed:
        return '진행완료';
    }
  }
}
