import 'package:greenap/models/challenge_category.dart';
import 'package:greenap/models/challenge_item.dart';

final List<ChallengeCategoryModel> dummyChallengeCategory = [
  ChallengeCategoryModel(
    id: 1,
    title: "자원절약",
    description: "일상에서 에너지와 자원을 아껴 탄소를 줄여요.",
    imageUrl: "assets/images/resource/resource.png",
    challenges: [
      ChallengeItemModel(
        id: 1,
        title: "멀티탭, 안 쓰는 조명 끄기",
        description: "대기전력을 차단해 전기 절약과 탄소 감축을 실천하는 챌린지입니다.",
        mainImageUrl: "assets/images/resource/resource_challenge1.png",
      ),
      ChallengeItemModel(
        id: 2,
        title: "배달 일회용품 줄이기",
        description: "배달 시 일회용 대신 다회용 식기를 쓰는 챌린지입니다.",
        mainImageUrl: "assets/images/resource/resource_challenge2.png",
      ),
      ChallengeItemModel(
        id: 3,
        title: "에어컨, 난방 끄고 환기 시키기",
        description: "에어컨이나 난방을 잠시 끄고 창문을 열어 환기하며 에너지를 아끼는 챌린지입니다.",
        mainImageUrl: "assets/images/resource/resource_challenge3.png",
      ),
      ChallengeItemModel(
        id: 4,
        title: "양치컵 사용하기",
        description: "양치할 때 물을 틀어놓지 않고 양치컵을 사용하는 챌린지입니다.",
        mainImageUrl: "assets/images/resource/resource_challenge4.png",
      ),
    ],
  ),
  ChallengeCategoryModel(
    id: 2,
    title: "재사용",
    description: "한 번 쓰고 버리는 물건 대신 다회용품을 사용해요.",
    imageUrl: "assets/images/reuse/reuse.png",
    challenges: [
      ChallengeItemModel(
        id: 5,
        title: "다회용 빨대 사용하기",
        description: "음료를 마실 때 일회용 대신 다회용 빨대를 사용하는 챌린지입니다.",
        mainImageUrl: "assets/images/reuse/reuse_challenge1.png",
      ),
      ChallengeItemModel(
        id: 6,
        title: "장바구니 사용하기",
        description: "장을 볼 때 비닐봉투 대신 장바구니를 사용하는 챌린지입니다.",
        mainImageUrl: "assets/images/reuse/reuse_challenge2.png",
      ),
      ChallengeItemModel(
        id: 7,
        title: "다회용 행주 사용하기",
        description: "키친타올이나 물티슈 대신 다회용 행주를 사용하는 챌린지입니다.",
        mainImageUrl: "assets/images/reuse/reuse_challenge3.png",
      ),
      ChallengeItemModel(
        id: 8,
        title: "텀블러 사용하기",
        description: "카페나 사무실에서 종이컵 대신 텀블러를 사용하는 챌린지입니다.",
        mainImageUrl: "assets/images/reuse/reuse_challenge4.png",
      ),
      ChallengeItemModel(
        id: 9,
        title: "전자 영수증 받기",
        description: "영수증을 종이로 받지 않고 대신 전자 영수증을 받는 챌린지입니다.",
        mainImageUrl: "assets/images/reuse/reuse_challenge5.png",
      ),
    ],
  ),
  ChallengeCategoryModel(
    id: 3,
    title: "친환경 소비",
    description: "지속가능한 소비 습관으로 환경을 지켜요.",
    imageUrl: "assets/images/eco/eco.png",
    challenges: [
      ChallengeItemModel(
        id: 10,
        title: "친환경 인증 마크 수집",
        description: "친환경 인증 마크가 있는 제품을 찾아 사용하는 챌린지입니다.",
        mainImageUrl: "assets/images/eco/eco_challenge1.png",
      ),
      ChallengeItemModel(
        id: 11,
        title: "SNS 사용 시간 단축",
        description: "SNS 사용 시간을 줄여 디지털 탄소까지 줄이는 습관을 만드는 챌린지입니다.",
        mainImageUrl: "assets/images/eco/eco_challenge2.png",
      ),
      ChallengeItemModel(
        id: 12,
        title: "이메일 정리",
        description: "불필요한 이메일을 정리해 저장된 디지털 탄소자료를 줄이는 챌린지입니다.",
        mainImageUrl: "assets/images/eco/eco_challenge3.png",
      ),
      ChallengeItemModel(
        id: 13,
        title: "채식 식사 인증",
        description: "1일 1끼 이상 채식 식사를 실천해 지구 자원 절약을 실천하는 챌린지입니다.",
        mainImageUrl: "assets/images/eco/eco_challenge4.png",
      ),
      ChallengeItemModel(
        id: 14,
        title: "유통기한 임박 식품 소비 인증",
        description: "유통기한이 임박한 식품을 소비하여 식량 자원 낭비를 줄이는 챌린지입니다.",
        mainImageUrl: "assets/images/eco/eco_challenge5.png",
      ),
    ],
  ),
  ChallengeCategoryModel(
    id: 4,
    title: "교통 절감",
    description: "이동 수단을 바꾸는 것만으로도 탄소를 줄일 수 있어요.",
    imageUrl: "assets/images/transport/transport.png",
    challenges: [
      ChallengeItemModel(
        id: 15,
        title: "도보 15분 이상 실천하기",
        description: "하루 15분 이상 걷기를 통해 차량 사용을 줄이고 탄소 배출을 낮추는 챌린지입니다.",
        mainImageUrl: "assets/images/transport/transport_challenge1.png",
      ),
      ChallengeItemModel(
        id: 16,
        title: "친환경적인 이동",
        description: "자전거나 전동 킥보드 등의 이동수단을 이용해 탄소 배출을 줄이는 챌린지입니다.",
        mainImageUrl: "assets/images/transport/transport_challenge2.png",
      ),
      ChallengeItemModel(
        id: 17,
        title: "계단 오르기",
        description: "엘리베이터 대신 계단을 이용해 에너지를 아끼고 건강을 챙기는 챌린지입니다.",
        mainImageUrl: "assets/images/transport/transport_challenge3.png",
      ),
      ChallengeItemModel(
        id: 18,
        title: "대중교통 이용하기",
        description: "자동차 대신 버스나 지하철을 타는 챌린지입니다.",
        mainImageUrl: "assets/images/transport/transport_challenge4.png",
      ),
      ChallengeItemModel(
        id: 19,
        title: "플로깅 실천하기",
        description: "조깅하면서 쓰레기를 줍는 플로깅으로 지역 환경을 깨끗하게 만드는 챌린지입니다.",
        mainImageUrl: "assets/images/transport/transport_challenge5.png",
      ),
    ],
  ),
];
