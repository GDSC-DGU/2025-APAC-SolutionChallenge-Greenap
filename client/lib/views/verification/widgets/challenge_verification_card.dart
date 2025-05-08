import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:get/get.dart';
import 'package:greenap/domain/models/challenge_item.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:greenap/views_model/verification/verification_view_model.dart';
import 'package:greenap/widgets/common/common_button.dart';

class ChallengeVerificationCard extends StatelessWidget {
  final MyChallengeModel challenge;
  ChallengeVerificationCard({super.key, required this.challenge});

  @override
  Widget build(BuildContext context) {
    final VerificationViewModel viewModel = Get.find();
    final ChallengeItemModel? challengeItem = viewModel.findChallengeItemById(
      challenge.challengeId,
    );

    return Container(
      decoration: BoxDecoration(
        color: ColorSystem.white,
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
      child: SizedBox(
        height: 150,

        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Row(
            children: [
              challengeItem != null
                  ? Image.asset(
                    challengeItem.mainImageUrl,
                    width: 110,
                    height: 110,
                    fit: BoxFit.cover,
                  )
                  : const SizedBox(width: 110, height: 110),
              const SizedBox(width: 8),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Row(
                      children: [
                        Text(
                          challenge.category,
                          style: FontSystem.Body2.copyWith(
                            color: ColorSystem.gray[700],
                          ),
                        ),
                        const Spacer(),
                        SvgPicture.asset(
                          'assets/icons/ice.svg',
                          width: 14,
                          height: 14,
                        ),
                        const SizedBox(width: 4),
                        challenge.iceCount > 0
                            ? Text(
                              '${challenge.iceCount}회 사용 가능',
                              style: FontSystem.Caption.copyWith(
                                color: ColorSystem.skyblue,
                              ),
                            )
                            : Text(
                              '얼리기 없음',
                              style: FontSystem.Caption.copyWith(
                                color: ColorSystem.skyblue,
                              ),
                            ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      challenge.title,
                      style: FontSystem.Head3.copyWith(
                        color: ColorSystem.gray[900],
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),

                    const Spacer(),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        challenge.iceCount > 0
                            ? CommonButton.outlined(
                              text: "얼리기",
                              onPressed: () {},
                            )
                            : const SizedBox.shrink(),
                        const SizedBox(width: 4),
                        CommonButton.primary(
                          text: "인증하기",
                          onPressed: () {
                            Get.toNamed(
                              '/verification-upload',
                              arguments: challenge.challengeId,
                            );
                          },
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
