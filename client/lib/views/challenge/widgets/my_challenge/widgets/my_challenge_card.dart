import 'package:flutter/material.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:get/get.dart';

class MyChallengeCard extends StatelessWidget {
  final MyChallengeModel myChallenge;

  const MyChallengeCard({super.key, required this.myChallenge});

  Color getStatusColor(ChallengeStatus status) {
    switch (status) {
      case ChallengeStatus.running:
        return ColorSystem.mint;
      case ChallengeStatus.completed:
        return Colors.grey;
      case ChallengeStatus.waiting:
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
      case ChallengeStatus.waiting:
        return "리포트 조회 대기중";
    }
  }

  @override
  Widget build(BuildContext context) {
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
            myChallenge.mainImageUrl?.isNotEmpty == true
                ? Image.network(
                  myChallenge.mainImageUrl!,
                  fit: BoxFit.contain,
                  width: 84,
                  height: 84,
                  errorBuilder: (context, error, stackTrace) {
                    return const Icon(Icons.image_not_supported);
                  },
                )
                : const Icon(Icons.image),
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
