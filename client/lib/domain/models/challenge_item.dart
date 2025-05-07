class ChallengeItemModel {
  final int id;
  final String title;
  final String description;
  final String mainImageUrl;

  ChallengeItemModel({
    required this.id,
    required this.title,
    required this.description,
    required this.mainImageUrl,
  });

  factory ChallengeItemModel.fromJson(Map<String, dynamic> json) {
    return ChallengeItemModel(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      mainImageUrl: json['main_image_url'] ?? '',
    );
  }
}
