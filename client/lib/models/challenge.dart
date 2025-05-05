import 'package:greenap/enums/challenge.dart';

class ChallengeModel {
  final int id;
  final String title;
  final String category;
  final int total_days;
  final int elapsed_days;
  final int progress;
  final int ice_count;
  final ChallengeCertificated is_cerficated_in_today;
  final ChallengeStatus status;
  final List certification_data_list;

  ChallengeModel({
    required this.id,
    required this.title,
    required this.category,
    required this.total_days,
    required this.elapsed_days,
    required this.progress,
    required this.ice_count,
    required this.is_cerficated_in_today,
    required this.status,
    required this.certification_data_list,
  });
}
