import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/views/category_detail/widgets/challenge_start_popup.dart';
import 'package:greenap/domain/models/challenge_report.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';
import 'package:greenap/views/category_detail/widgets/challenge_detail_popup.dart';
import 'package:greenap/views_model/my_challenge_detail/my_challenge_detail_view_model.dart';
import 'challenge_restart_popup.dart';

class ReportView extends StatelessWidget {
  ReportView({super.key});
  final MyChallengeDetailViewModel viewModel = Get.find();

  @override
  Widget build(BuildContext context) {
    if (viewModel.challengeReport == null) {
      // 비동기 호출을 다음 프레임으로 미룸 (중복 호출 방지)
      Future.microtask(() => viewModel.fetchReport());
    }

    return GetBuilder<MyChallengeDetailViewModel>(
      builder: (_) {
        final report = viewModel.challengeReport;

        if (report == null) {
          return const Center(child: CircularProgressIndicator());
        }

        return Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            _buildTitle(report),
            const SizedBox(height: 12),
            _buildReportCard(context, report),
            const SizedBox(height: 20),
          ],
        );
      },
    );
  }

  Widget _buildTitle(ChallengeReportModel report) {
    return Text(
      "챌린지 리포트",
      style: FontSystem.Head2.copyWith(color: ColorSystem.gray[700]),
    );
  }

  Widget _buildReportCard(BuildContext context, ChallengeReportModel report) {
    return Container(
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.08),
            spreadRadius: 2,
            blurRadius: 4,
          ),
        ],
        borderRadius: BorderRadius.circular(8),
        color: ColorSystem.white,
      ),
      child: Column(
        children: [
          Text(
            "참여 기간 중 총 ${report.successDays}회 인증을 완료했습니다!",
            style: FontSystem.Body2Bold.copyWith(color: ColorSystem.gray[700]),
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
          const SizedBox(height: 24),
          _buildMessageBox(report),
          const SizedBox(height: 24),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              _MetricCard(
                title: "연속 일수",
                value: "${report.maxConsecutiveDays}일",
              ),
              const SizedBox(width: 24),
              _MetricCard(title: "챌린저 랭킹", value: "${report.ranking}위"),
            ],
          ),
          const SizedBox(height: 20),
          _buildButtons(context, report),
        ],
      ),
    );
  }

  Widget _buildMessageBox(ChallengeReportModel report) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 12),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(4),
        color: ColorSystem.white.withOpacity(0.5),
        boxShadow: [
          BoxShadow(
            color: ColorSystem.mint.withOpacity(0.1),
            spreadRadius: 0,
            blurRadius: 4,
          ),
        ],
      ),
      child: Text(
        report.reportMessage,
        style: FontSystem.Body3.copyWith(color: ColorSystem.mint),
      ),
    );
  }

  Widget _buildButtons(BuildContext context, ChallengeReportModel report) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        PopupActionButton(
          text: "저장하기",
          onPressed: () {},
          type: ButtonStyleType.outlined,
        ),
        const SizedBox(width: 12),
        PopupActionButton(
          text: report.status != "WAITING" ? "재도전 하기" : "계속 도전하기",
          onPressed: () => _handleJoinChallenge(context, report),
        ),
      ],
    );
  }

  void _handleJoinChallenge(
    BuildContext context,
    ChallengeReportModel report,
  ) async {
    final viewModel = Get.find<MyChallengeDetailViewModel>();
    final challengeId = viewModel.challenge.value!.id;
    await viewModel.fetchChallengeDetail(challengeId);
    final challengeDetail = viewModel.challengeDetail.value;
    print(challengeDetail);

    if (challengeDetail != null) {
      showDialog(
        context: context,
        builder:
            (_) => ChallengeDetailPopup(
              challenge: challengeDetail,
              onCancel: () => Navigator.pop(context),
              onJoin: (selectedDuration) async {
                Navigator.pop(context);
                await viewModel.joinChallenge(
                  challengeDetail.id,
                  selectedDuration,
                );

                Future.delayed(Duration.zero, () {
                  showDialog(
                    context: context,
                    builder:
                        (_) =>
                            report.status == "WAITING"
                                ? ChallengeRestartPopup(
                                  challenge: challengeDetail,
                                  selectedDuration: selectedDuration,
                                  onChecked: () {
                                    Navigator.pop(context);
                                    Get.offAllNamed(
                                      '/root',
                                      arguments: {'initialTab': 2},
                                    );
                                  },
                                )
                                : ChallengeStartPopup(
                                  challenge: challengeDetail,
                                  selectedDuration: selectedDuration,
                                  onChecked: () => Navigator.pop(context),
                                  goVerification: () => Navigator.pop(context),
                                ),
                  );
                });
              },
            ),
      );
    } else {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('챌린지 정보를 불러오지 못했습니다.')));
    }
  }
}

class _MetricCard extends StatelessWidget {
  final String title;
  final String value;

  const _MetricCard({required this.title, required this.value});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(4),
        color: ColorSystem.white,
        boxShadow: [
          BoxShadow(
            color: ColorSystem.black.withOpacity(0.08),
            spreadRadius: 2,
            blurRadius: 4,
          ),
        ],
      ),
      child: Column(
        children: [
          Text(
            title,
            style: FontSystem.Head3.copyWith(color: ColorSystem.gray[700]),
          ),
          const SizedBox(height: 12),
          Text(
            value,
            style: FontSystem.Head3.copyWith(color: ColorSystem.mint),
          ),
        ],
      ),
    );
  }
}
