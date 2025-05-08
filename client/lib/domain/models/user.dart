class User {
  final String nickname;
  final String profileImageUrl;

  User({required this.nickname, required this.profileImageUrl});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      nickname: json['nickname'],
      profileImageUrl: json['profile_image_url'],
    );
  }
}
