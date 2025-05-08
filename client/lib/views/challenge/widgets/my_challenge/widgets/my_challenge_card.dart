import 'package:flutter/material.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/domain/models/challenge_category.dart';
import 'package:get/get.dart';

class MyChallengeCard extends StatelessWidget {
  final MyChallengeModel myChallenge;
  final List<ChallengeCategoryModel> allChallenges;

  const MyChallengeCard({
    super.key,
    required this.myChallenge,
    required this.allChallenges,
  });

  Color getStatusColor(ChallengeStatus status) {
    switch (status) {
      case ChallengeStatus.running:
        return ColorSystem.mint!;
      case ChallengeStatus.completed:
        return Colors.grey;
    }
  }

  void _navigateToDetail() {
    Get.toNamed('/my-challenge-detail', arguments: myChallenge);
  }

  String getStatusLabel(ChallengeStatus status) {
    switch (status) {
      case ChallengeStatus.running:
        return "진행중";
      case ChallengeStatus.completed:
        return "진행완료";
    }
  }

  String? getImageUrlByChallengeId(
    int challengeId,
    List<ChallengeCategoryModel> categories,
  ) {
    for (final category in categories) {
      for (final challenge in category.challenges) {
        if (challenge.id == challengeId) {
          return challenge.mainImageUrl;
        }
      }
    }
    return null;
  }

  @override
  Widget build(BuildContext context) {
    final imageUrl = getImageUrlByChallengeId(
      myChallenge.challengeId,
      allChallenges,
    );

    final cardContent = Container(
      decoration: BoxDecoration(
        color: ColorSystem.white,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.08),
            spreadRadius: 2, // 그림자 퍼지는 정도
            blurRadius: 4, // 그림자 흐림 정도
            offset: Offset(0, 0), // 그림자 위치 (x축, y축)
          ),
        ],
      ),
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Row(
          children: [
            imageUrl != null
                ? Image.asset(
                  imageUrl,
                  fit: BoxFit.contain,
                  width: 84,
                  height: 84,
                )
                : const Text(""),
            SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        myChallenge.category,
                        style: FontSystem.Body2.copyWith(
                          color: ColorSystem.gray[600],
                        ),
                      ),
                      Text(
                        getStatusLabel(myChallenge.status),
                        style: FontSystem.Caption.copyWith(
                          color: ColorSystem.mint,
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 4),
                  Text(
                    myChallenge.title,
                    style: FontSystem.Head3.copyWith(
                      color: ColorSystem.gray[700],
                    ),
                  ),
                  const SizedBox(height: 12),

                  myChallenge.status == ChallengeStatus.running
                      ? Column(
                        children: [
                          Align(
                            alignment: Alignment.topRight,
                            child: Text(
                              '${myChallenge.progress}%',
                              style: FontSystem.Body3Blod.copyWith(
                                color: ColorSystem.mint,
                              ),
                            ),
                          ),
                          LinearProgressIndicator(
                            minHeight: 8,
                            borderRadius: BorderRadius.circular(8),
                            value:
                                myChallenge.elapsedDays / myChallenge.totalDays,
                            backgroundColor: ColorSystem.mint.withOpacity(0.2),
                            color: ColorSystem.mint,
                          ),
                        ],
                      )
                      : Align(
                        alignment: Alignment.topRight,
                        child: GestureDetector(
                          onTap: _navigateToDetail,
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
                              '리포트 보기',
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
      ),
    );

    if (myChallenge.status == ChallengeStatus.running) {
      return GestureDetector(onTap: _navigateToDetail, child: cardContent);
    } else {
      return cardContent;
    }
  }
}
