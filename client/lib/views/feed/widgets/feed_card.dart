import 'package:flutter/material.dart';
import 'package:greenap/config/color_system.dart';
import 'package:greenap/config/font_system.dart';
import 'package:greenap/domain/models/feed_item.dart';
import 'package:intl/intl.dart';

class FeedCard extends StatelessWidget {
  final FeedItemModel feed;
  final bool isMine;
  final VoidCallback? onEdit;
  final VoidCallback? onDelete;

  const FeedCard({
    super.key,
    required this.feed,
    this.isMine = false,
    this.onEdit,
    this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
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
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(vertical: 4, horizontal: 8),
                decoration: BoxDecoration(
                  color: ColorSystem.mint,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  feed.category,
                  style: FontSystem.Caption.copyWith(color: ColorSystem.white),
                ),
              ),
              Text(
                DateFormat('yyyy-MM-dd HH:mm').format(feed.createdAt),
                style: FontSystem.Caption.copyWith(
                  color: ColorSystem.gray[500],
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            feed.challengeTitle,
            style: FontSystem.Head3.copyWith(color: ColorSystem.gray[800]),
          ),
          ListTile(
            contentPadding: EdgeInsets.zero,

            leading: CircleAvatar(
              backgroundImage: NetworkImage(feed.user.profileImageUrl),
            ),
            title: Text(feed.user.nickname),
            subtitle: Text(feed.content),
          ),
          Image.network(
            feed.imageUrl,
            fit: BoxFit.cover,
            width: double.infinity,
            height: 300,
          ),
        ],
      ),
    );
  }
}
