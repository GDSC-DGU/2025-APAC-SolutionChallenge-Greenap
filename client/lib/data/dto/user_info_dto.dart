import 'package:greenap/domain/models/user.dart';

class JwtTokenDto {
  final String accessToken;
  final String refreshToken;

  JwtTokenDto({required this.accessToken, required this.refreshToken});

  factory JwtTokenDto.fromJson(Map<String, dynamic> json) {
    return JwtTokenDto(
      accessToken: json['accessToken'],
      refreshToken: json['refreshToken'],
    );
  }
}

class UserInfoDto {
  final String nickname;
  final String email;
  final String profileImageUrl;
  final JwtTokenDto jwtTokenDto;

  UserInfoDto({
    required this.nickname,
    required this.email,
    required this.profileImageUrl,
    required this.jwtTokenDto,
  });

  factory UserInfoDto.fromJson(Map<String, dynamic> json) {
    return UserInfoDto(
      nickname: json['nickname'],
      email: json['email'],
      profileImageUrl: json['profileImageUrl'],
      jwtTokenDto: JwtTokenDto.fromJson(json['jwtTokenDto']),
    );
  }

  UserModel toModel() {
    return UserModel(nickname: nickname, profileImageUrl: profileImageUrl);
  }
}
