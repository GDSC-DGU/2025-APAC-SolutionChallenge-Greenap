import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/domain/models/challenge_item.dart';
import 'challenge_detail_popup.dart';
import 'package:greenap/views_model/category_detail/category_detail_view_model.dart';
import 'package:get/get.dart';
import 'challenge_start_popup.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';
import 'package:greenap/views_model/challenge/my_challenge_view_model.dart';

class ChallengeCard extends StatelessWidget {
  final ChallengeItemModel challenge;
  ChallengeCard({super.key, required this.challenge});

  void _showDetailPopup(BuildContext context) async {
    final viewModel = Get.find<CategoryDetailViewModel>();
    final myChallengeViewModel = Get.find<MyChallengeViewModel>();
    await viewModel.fetchChallengeDetail(challenge.id);

    final detail = viewModel.challengeDetail.value;
    if (detail == null) {
      return;
    }

    showDialog(
      context: Get.context!,
      builder:
          (_) => ChallengeDetailPopup(
            challenge: detail,
            onCancel: () => Navigator.pop(context),
            onJoin: (duration) async {
              Navigator.pop(Get.context!);
              final response = await viewModel.joinChallenge(
                challengeId: challenge.id,
                duration: duration,
              );
              if (response.data == null) {
                // 다음 프레임에서 실행되도록 함
                Future.delayed(Duration.zero, () {
                  showDialog(
                    context: context,
                    builder:
                        (_) => BasePopupDialog(
                          title: response.message,
                          actions: [
                            PopupActionButton(
                              text: '확인',
                              type: ButtonStyleType.disabled,
                              onPressed: () {
                                Navigator.pop(context);
                              },
                            ),
                          ],
                        ),
                  );
                });
              } else {
                await myChallengeViewModel.loadMyChallenges();

                // ✨ id로 해당 챌린지 찾아서 넘겨주기
                final myChallenge = myChallengeViewModel.myChallenges
                    .firstWhere(
                      (c) => c.challengeId == challenge.id,
                      orElse: () => throw Exception('마이 챌린지에서 해당 챌린지를 찾을 수 없음'),
                    );

                // 다음 프레임에서 실행되도록 함
                Future.delayed(Duration.zero, () {
                  showDialog(
                    context: context,
                    builder:
                        (_) => ChallengeStartPopup(
                          challenge: detail,
                          selectedDuration: duration,
                          onChecked: () {
                            Navigator.pop(context);
                          },
                          goVerification: () {
                            Navigator.pop(context);
                            Get.toNamed(
                              '/verification-upload',
                              arguments: {
                                'challengeId': challenge.id,
                                'userChallengeId': myChallenge.id,
                                'myChallengeModel': myChallenge,
                              },
                            );
                          },
                        ),
                  );
                });
              }
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
          Image.network(
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
