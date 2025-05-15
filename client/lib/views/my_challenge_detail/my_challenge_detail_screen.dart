import 'package:flutter/material.dart';
import 'package:greenap/domain/models/my_challenge.dart';
import 'package:greenap/domain/enums/challenge.dart';
import 'package:greenap/views/base/base_screen.dart';
import 'package:greenap/views_model/my_challenge_detail/my_challenge_detail_view_model.dart';
import 'package:greenap/widgets/app_bar/back_app_bar.dart';
import './widgets/verification_day_list.dart';
import './widgets/report_view.dart';
import './widgets/feed_view.dart';
import 'package:get/get.dart';

class MyChallengeDetailScreen extends BaseScreen<MyChallengeDetailViewModel> {
  final MyChallengeModel challenge = Get.arguments as MyChallengeModel;

  MyChallengeDetailScreen({super.key});

  @override
  PreferredSizeWidget buildAppBar(BuildContext context) {
    return BackAppBar(title: viewModel.challenge.value!.title);
  }

  @override
  Widget buildBody(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Obx(() {
        final detail = viewModel.challengeDetail.value;

        if (detail == null) {
          return Center(child: CircularProgressIndicator());
        }

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            VerificationDayListView(
              totalDays: challenge.totalDays,
              certificationDataList: challenge.certificationDataList,
            ),
            const SizedBox(height: 24),
            challenge.status == ChallengeStatus.completed ||
                    challenge.status == ChallengeStatus.waiting ||
                    challenge.status == ChallengeStatus.pending
                ? ReportView()
                : FeedView(),
          ],
        );
      }),
    );
  }
}
