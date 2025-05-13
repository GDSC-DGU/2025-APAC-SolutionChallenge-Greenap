import 'package:flutter/material.dart';
import 'package:greenap/config/Font_system.dart';
import 'package:greenap/config/color_system.dart';
import 'package:get/get.dart';
import 'package:greenap/views_model/verification/verification_view_model.dart';
import 'package:greenap/views_model/challenge/my_challenge_view_model.dart';
import 'package:greenap/widgets/app_bar/default_app_bar.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/views/base/base_screen.dart';
import './widgets/ice_description_card.dart';
import './widgets/challenge_verification_card.dart';

class VerificationScreen extends BaseScreen<VerificationViewModel> {
  final MyChallengeViewModel myChallengeViewModel = Get.find();
  final VerificationViewModel verificationViewModel = Get.find();

  VerificationScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return DefaultAppBar(title: '챌린지 인증');
  }

  @override
  Widget buildBody(BuildContext context) {
    return Obx(() {
      final challenges =
          myChallengeViewModel.myChallenges
              .where((c) => c.status == ChallengeStatus.running)
              .toList();

      return SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              IceDescriptionCard(),
              const SizedBox(height: 24),
              Text(
                '참여 중인 챌린지',
                style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
              ),
              const SizedBox(height: 12),
              if (challenges.isEmpty)
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 40),
                  child: Center(
                    child: Text(
                      '현재 참여 중인 챌린지가 없습니다.',
                      style: FontSystem.Body1.copyWith(
                        color: ColorSystem.gray[500],
                      ),
                    ),
                  ),
                )
              else
                Column(
                  children:
                      challenges.map((challenge) {
                        return Padding(
                          padding: const EdgeInsets.only(bottom: 12),
                          child: ChallengeVerificationCard(
                            challenge: challenge,
                          ),
                        );
                      }).toList(),
                ),
            ],
          ),
        ),
      );
    });
  }
}
