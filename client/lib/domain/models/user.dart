class UserModel {
  final String nickname;
  final String profileImageUrl;

  UserModel({required this.nickname, required this.profileImageUrl});

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      nickname: json['nickname'],
      profileImageUrl: json['profileImageUrl'],
    );
  }
}
