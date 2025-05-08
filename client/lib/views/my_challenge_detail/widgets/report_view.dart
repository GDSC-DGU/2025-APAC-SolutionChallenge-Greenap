import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';

class ReportView extends StatelessWidget {
  final ChallengeReportModel report;

  const ReportView({super.key, required this.report});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Text(
          "챌린지 리포트",
          style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
        ),
        const SizedBox(height: 12),
        Container(
          padding: EdgeInsets.all(16),
          decoration: BoxDecoration(
            boxShadow: [
              BoxShadow(
                color: ColorSystem.black.withOpacity(0.08),
                spreadRadius: 2,
                blurRadius: 4,
                offset: Offset(0, 0),
              ),
            ],
            borderRadius: BorderRadius.circular(8),
            color: ColorSystem.white,
          ),
          child: Column(
            children: [
              Text(
                "참여 기간 중 총 ${report.successDays}회 인증을 완료했습니다!",
                style: FontSystem.Body2Bold.copyWith(
                  color: ColorSystem.gray[700],
                ),
              ),
              const SizedBox(height: 24),
              Image.asset(
                'assets/images/challenge_report.png',
                width: 120,
                height: 120,
              ),
              const SizedBox(height: 24),
              Text(
                '탄소 절감량',
                style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
              ),
              SizedBox(height: 24),
              SizedBox(
                width: double.infinity,
                child: Container(
                  padding: const EdgeInsets.symmetric(
                    vertical: 16,
                    horizontal: 12,
                  ),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(4),
                    color: ColorSystem.white.withOpacity(0.5),
                    boxShadow: [
                      BoxShadow(
                        color: ColorSystem.mint.withOpacity(0.1),
                        spreadRadius: 0,
                        blurRadius: 4,
                        offset: Offset(0, 0),
                      ),
                    ],
                  ),
                  child: Text(
                    report.reportMessage,
                    style: FontSystem.Body3.copyWith(color: ColorSystem.mint),
                  ),
                ),
              ),
              SizedBox(height: 24),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,

                children: [
                  Container(
                    padding: EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(4),
                      color: ColorSystem.white,
                      boxShadow: [
                        BoxShadow(
                          color: ColorSystem.black.withOpacity(0.08),
                          spreadRadius: 2,
                          blurRadius: 4,
                          offset: Offset(0, 0),
                        ),
                      ],
                    ),
                    child: Column(
                      children: [
                        Text(
                          "연속 일수",
                          style: FontSystem.Head3.copyWith(
                            color: ColorSystem.gray[700],
                          ),
                        ),
                        const SizedBox(height: 12),
                        Text(
                          "${report.maxConsecutiveDays}일",
                          style: FontSystem.Head3.copyWith(
                            color: ColorSystem.mint,
                          ),
                        ),
                      ],
                    ),
                  ),
                  SizedBox(width: 24),
                  Container(
                    padding: EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(4),
                      color: ColorSystem.white,
                      boxShadow: [
                        BoxShadow(
                          color: ColorSystem.black.withOpacity(0.08),
                          spreadRadius: 2,
                          blurRadius: 4,
                          offset: Offset(0, 0),
                        ),
                      ],
                    ),
                    child: Column(
                      children: [
                        Text(
                          "챌린저 랭킹",
                          style: FontSystem.Head3.copyWith(
                            color: ColorSystem.gray[700],
                          ),
                        ),
                        const SizedBox(height: 12),
                        Text(
                          "${report.ranking}위",
                          style: FontSystem.Head3.copyWith(
                            color: ColorSystem.mint,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  PopupActionButton(
                    text: "저장하기",
                    onPressed: () {},
                    type: ButtonStyleType.outlined,
                  ),
                  SizedBox(width: 12),
                  PopupActionButton(
                    text: report.status != "COMPLETED" ? "재도전 하기" : "계속 도전하기",
                    onPressed: () {},
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }
}
