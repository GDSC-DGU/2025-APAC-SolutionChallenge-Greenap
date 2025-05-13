import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/widgets/common/base_popup_dialog.dart';
import 'package:greenap/widgets/common/popup_action_button.dart';
import 'package:greenap/domain/models/challenge_detail.dart';

class ChallengeDetailPopup extends StatefulWidget {
  final ChallengeDetailModel challenge;
  final VoidCallback onCancel;
  final void Function(int selectedDuration) onJoin;

  const ChallengeDetailPopup({
    super.key,
    required this.challenge,
    required this.onCancel,
    required this.onJoin,
  });

  @override
  State<ChallengeDetailPopup> createState() => _ChallengeDetailPopupState();
}

class _ChallengeDetailPopupState extends State<ChallengeDetailPopup> {
  late int selectedDuration;

  @override
  void initState() {
    super.initState();
    selectedDuration = widget.challenge.participationDates.first;
  }

  @override
  Widget build(BuildContext context) {
    final maxHeight = MediaQuery.of(context).size.height * 0.65;

    return BasePopupDialog(
      title: widget.challenge.title,
      actions: [
        PopupActionButton(
          text: '취소하기',
          type: ButtonStyleType.disabled,
          onPressed: widget.onCancel,
        ),
        PopupActionButton(
          text: '참여하기',
          type: ButtonStyleType.primary,
          onPressed: () => widget.onJoin(selectedDuration),
        ),
      ],
      child: ConstrainedBox(
        constraints: BoxConstraints(maxHeight: maxHeight),
        child: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildImage(),
              const SizedBox(height: 16),
              _buildDescription(),
              const SizedBox(height: 20),
              _buildCertMethod(),
              const SizedBox(height: 20),
              _buildDurationDropdown(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildImage() {
    return Center(
      child: Image.network(
        widget.challenge.mainImageUrl,
        width: 120,
        height: 120,
      ),
    );
  }

  Widget _buildDescription() {
    return Text(
      widget.challenge.description,
      style: FontSystem.Body2.copyWith(color: ColorSystem.gray[700]),
    );
  }

  Widget _buildCertMethod() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          '챌린지 인증 방법',
          style: FontSystem.Body1Bold.copyWith(color: ColorSystem.gray[900]),
        ),
        const SizedBox(height: 12),
        Text(
          widget.challenge.certificationMethodDescription,
          style: FontSystem.Body2.copyWith(color: ColorSystem.gray[700]),
        ),
      ],
    );
  }

  Widget _buildDurationDropdown() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          '참여기간 선택',
          style: FontSystem.Body1Bold.copyWith(color: ColorSystem.gray[900]),
        ),
        const SizedBox(height: 12),
        DropdownButtonFormField<int>(
          value: selectedDuration,
          items:
              widget.challenge.participationDates.map((int value) {
                return DropdownMenuItem<int>(
                  value: value,
                  child: Text('$value일', style: FontSystem.Body2),
                );
              }).toList(),
          onChanged: (newValue) {
            if (newValue != null) {
              setState(() => selectedDuration = newValue);
            }
          },
          decoration: InputDecoration(
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
            contentPadding: const EdgeInsets.symmetric(
              horizontal: 16,
              vertical: 12,
            ),
          ),
        ),
      ],
    );
  }
}
