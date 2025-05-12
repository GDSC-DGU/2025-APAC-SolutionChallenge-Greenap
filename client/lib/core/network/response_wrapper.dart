class ResponseWrapper<T> {
  final String code;
  final String? message;
  final T? data;

  ResponseWrapper({required this.code, this.message, this.data});

  factory ResponseWrapper.fromJson(Map<String, dynamic> json) {
    return ResponseWrapper(
      code: json['code'],
      data: json['data'],
      message: json['error']?['message'],
    );
  }
}
