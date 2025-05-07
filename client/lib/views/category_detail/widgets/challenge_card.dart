import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/domain/models/challenge_item.dart';
import 'package:greenap/domain/models/dummy/challenge_detail_dummy.dart';
import 'challenge_detail_popup.dart';
import 'package:greenap/domain/models/challenge_detail.dart';
import 'package:get/get.dart';

class ChallengeCard extends StatelessWidget {
  final ChallengeItemModel challenge;
  ChallengeCard({super.key, required this.challenge});

  void _showDetailPopup(BuildContext context) {
    print('현재 챌린지 ID: ${challenge.id}');
    print(
      'dummyChallengeDetails ID 목록: ${dummyChallengeDetails.map((e) => e.id).toList()}',
    );

    final ChallengeDetailModel detail = dummyChallengeDetails.firstWhere(
      (e) => e.id == challenge.id,
      orElse: () => throw Exception('해당 챌린지의 상세 정보를 찾을 수 없습니다.'),
    );

    showDialog(
      context: Get.context!,
      builder:
          (_) => ChallengeDetailPopup(
            challenge: detail,
            onCancel: () => Navigator.pop(context),
            onJoin: () {
              // 참여 처리 로직 추가 가능
              Navigator.pop(context);
            },
          ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: ColorSystem.white,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.08),
            spreadRadius: 2,
            blurRadius: 4,
            offset: Offset(0, 0),
          ),
        ],
      ),
      child: Row(
        children: [
          Image.asset(
            challenge.mainImageUrl,
            width: 110,
            height: 110,
            fit: BoxFit.cover,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      challenge.title,
                      style: FontSystem.Head3.copyWith(
                        color: ColorSystem.gray[800],
                      ),
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      challenge.description,
                      style: FontSystem.Body3.copyWith(
                        color: ColorSystem.gray[800],
                      ),
                      overflow: TextOverflow.ellipsis,
                      maxLines: 2,
                    ),
                  ],
                ),
                SizedBox(height: 10),
                Align(
                  alignment: Alignment.bottomRight,
                  child: GestureDetector(
                    onTap: () => _showDetailPopup(context),
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        vertical: 4,
                        horizontal: 12,
                      ),
                      decoration: BoxDecoration(
                        color: ColorSystem.mint,
                        borderRadius: BorderRadius.circular(4),
                      ),
                      child: Text(
                        '자세히 보기',
                        style: FontSystem.Button3.copyWith(
                          color: ColorSystem.white,
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
