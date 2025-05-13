INSERT
IGNORE INTO challenge_categories
(challenge_category_id, title, description, image_url, created_at, updated_at, deleted_at)
VALUES (1,
        '자원 절약',
        '자원 절약을 통해 에너지를 아낄 수 있습니다.',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/category/1.png',
        '2025-04-21 00:00:00',
        '2025-04-21 00:00:00',
        NULL),
       (2,
        '재사용',
        '재사용을 통해 자원을 아낄 수 있습니다.',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/category/2.png',
        '2025-04-21 00:00:00',
        '2025-04-21 00:00:00',
        NULL),
       (3,
        '친환경',
        '친환경 제품을 사용하여 환경을 보호합니다.',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/category/3.png',
        '2025-04-21 00:00:00',
        '2025-04-21 00:00:00',
        NULL),
       (4,
        '교통 절감',
        '교통 절감을 통해 에너지를 아낄 수 있습니다.',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/category/4.png',
        '2025-04-21 00:00:00',
        '2025-04-21 00:00:00',
        NULL);


INSERT
IGNORE INTO challenges
(challenge_id, challenge_category_id, title, pre_description, description, certification_method_description,
 main_image_url, certification_example_image_url, created_at, updated_at, deleted_at)
VALUES (1,
        1,
        '멀티탭, 안 쓰는 조명 끄기',
        '대기전력을 차단해 전기 절약과 탄소 감축을 실천하는 챌린지입니다.',
        '사용하지 않는 전자기기의 전원을 끄거나, 플러그를 뽑는 챌린지입니다. TV, 컴퓨터 등의 대기 전력을 차단함으로써 불필요한 전기 낭비를 줄이고 온실가스 배출을 저감할 수 있습니다.',
        '멀티탭 전원 OFF 상태 사진, 또는 플러그가 뽑힌 상태를 찍은 사진으로 인증',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/1.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/1.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (2,
        1,
        '배달 일회용품 줄이기',
        '배달 시 일회용 대신 다회용 식기를 쓰는 챌린지입니다.',
        '배달 음식을 받을 때 일회용 수저, 포크, 나이프 등의 제공을 거절하고, 집에 있는 다회용 식기를 사용하거나 일회용 용기를 사용하지 않고 도시락을 사용하는 챌린지입니다.',
        '수저, 포크 등을 요청하지 않고 받은 배달 음식 사진 / 도시락을 포장해서 온 사진',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/2.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/2.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (3,
        1,
        '에어컨, 난방 끄고 환기 시키기',
        '에어컨이나 난방을 잠시 끄고 창문을 열어 환기하며 에너지를 아끼는 챌린지입니다.',
        '실내 온도 조절을 위해 에어컨이나 난방 기기를 잠시 꺼두고 창문을 열어 환기를 하는 챌린지입니다. 하루 30분 정도의 환기만으로도 실내 공기 질을 개선하고 에너지 사용을 줄이는 데 효과적입니다.',
        '창문, 문을 열어 환기 중인 모습 또는 에어컨/난방이 꺼져 있는 조작 패널/벽면 사진으로 인증',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/3.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/3.jpeg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (4,
        1,
        '양치컵 사용하기',
        '양치할 때 물을 틀어놓지 않고 양치컵을 사용하는 챌린지입니다.',
        '양치할 때 물을 틀어놓지 않고 양치컵을 사용하는 챌린지입니다. 양치할 때 양치컵 하나만 사용하더라도 6~12L의 물을 절약할 수 있습니다.',
        '양치컵에 물을 받아 사용하는 모습 인증.',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/4.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/4.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (5,
        2,
        '다회용 빨대 사용하기',
        '음료를 마실 때 일회용 대신 다회용 빨대를 쓰는 챌린지입니다.',
        '음료를 마실 때 일회용 빨대 대신 스테인리스나 실리콘 등 다회용 빨대를 사용하는 챌린지입니다.
   매일 다회용 빨대를 사용하는 것만으로 1년에 약 1.3kg에 달하는 플라스틱 사용을 줄일 수 있으며, 바다로 흘러가는 미세 플라스틱을 막아 수많은 해양 생물의 생명을 지킬 수 있습니다.',
        '다회용 빨대를 사용 중인 음료 컵 사진 또는 다회용 빨대 단독 사진으로 인증',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/5.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/5.png',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (6,
        2,
        '장바구니 사용하기',
        '장을 보거나 쇼핑할 때 비닐봉투 대신 장바구니를 사용하는 챌린지입니다.',
        '장을 보거나 쇼핑할 때 비닐봉투 대신 장바구니를 사용하는 챌린지입니다. 1년에 평균 500장 이상 쓰이는 일회용 비닐봉투를 줄이는 것만으로도,수 백 마리의 해양 생물이 위협받는 플라스틱 쓰레기를 줄일 수 있습니다.',
        '장바구니를 들고 있는 사진 혹은 장바구니에 담긴 물건 사진으로 인증',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/6.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/6.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (7,
        2,
        '다회용 행주 사용하기',
        '키친타월이나 물티슈 대신 다회용 행주를 사용하는 챌린지입니다.',
        '키친타월이나 물티슈 대신 다회용 행주를 사용하는 챌린지입니다.
   매일 손이 가는 만큼 무심코 버려지는 일회용 섬유들이 많은데, 행주 하나만 바꿔도 연간 수천 장의 폐기물을 막고 미세플라스틱 배출도 함께 줄일 수 있습니다. ',
        '테이블 등을 행주로 닦는 모습, 행주를 사용하는 장면, 행주 단독 사진으로 인증',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/7.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/7.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (8,
        2,
        '텀블러 사용하기',
        '카페나 사무실에서 종이컵 대신 텀블러를 사용하는 챌린지입니다.',
        '카페나 사무실에서 종이컵 대신 텀블러를 사용하는 챌린지입니다.
   텀블러 하나로 1년에 약 300개 이상의 일회용 컵 사용을 줄일 수 있으며, 이는 물 수천 리터와 나무 수십 그루의 자원을 보호하는 효과로 이어집니다.',
        '텀블러에 음료를 담은 사진, 텀블러와 함께한 일상 사진으로 인증',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/8.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/8.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (9,
        2,
        '전자 영수증 받기',
        '영수증을 받을 때 종이 영수증 대신 전자영수증을 요청하는 챌린지입니다.',
        '물건을 사고 영수증을 받을 때 종이 영수증 대신 전자영수증을 요청하는 챌린지입니다.
   우리가 무심코 받는 종이 한 장에는 잉크, 코팅, 화학물질, 자원이 들어있고, 연간 수십억 장이 버려지고 있습니다. 당신의 선택 하나로, 나무와 물, 공기를 지킬 수 있습니다.',
        '전자영수증 문자/앱 화면 캡처 사진(당일 날짜로 된 영수증만 가능)',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/9.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/9.png',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (10,
        3,
        '친환경 인증 마크 수집',
        '친환경 인증 마크가 있는 제품을 찾아 지속 가능한 소비를 실천하는 챌린지입니다.',
        '우리는 매일 수많은 소비를 합니다. 이 챌린지는 ‘친환경 인증 마크’가 붙은 제품을 찾아보고 인증하는 과정을 통해,
   사용자가 환경 친화적인 소비를 습관화하고, 어떤 제품이 지속 가능한 선택인지 시각적으로 인지할 수 있도록 돕습니다.

   인증 마크는 탄소발자국, 재활용 인증, 무독성 성분, 친환경 포장 등을 나타내며, 그 자체로 탄소배출 감소, 오염 방지 효과를 갖습니다.',
        '제품 사진 + 인증 마크가 명확하게 보이도록 촬영1일 1건 업로드 가능',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/10.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/10.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (11,
        3,
        'SNS 사용 시간 단축',
        'SNS 사용 시간을 줄여 디지털 탄소발자국을 낮추는 습관을 만드는 챌린지입니다.',
        '스마트폰을 사용할수록 배터리 소모는 증가하고, 그 배터리를 충전하는 데 필요한 전기도 결국 탄소를 발생시킵니다.

   특히 영상·SNS 앱은 데이터 트래픽도 크고, 화면도 오래 켜져 있어 환경에 미치는 영향이 결코 적지 않아요.

   일상 속에서 SNS 사용 시간을 의식적으로 줄이고, 디지털 탄소발자국을 줄이는 습관을 형성하는 데 목적이 있습니다.',
        '스마트폰 ‘스크린 타임’ 화면 캡쳐
   인증 기준: 어제 대비 SNS 사용 시간 감소한 것을 보여주는 캡처
   예시:
       ◦ 어제 인스타그램 1시간 15분 → 오늘 45분 = 챌린지 성공',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/11.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/11.jpeg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (12,
        3,
        '이메일 정리',
        '불필요한 이메일과 파일을 정리해 디지털 탄소발자국을 줄이는 챌린지입니다.',
        '편지함에 쌓인 수천 개의 이메일,
   그 자체로 전력과 서버 저장 공간을 소모하며 탄소를 발생시킵니다.
   이 챌린지는 매일 불필요한 이메일을 삭제하고, 클라우드 저장공간을 비움으로써 디지털 탄소발자국을 줄이는 습관을 만드는 것을 목표로 합니다.',
        '메일 앱 또는 웹에서 "삭제 전과 후의 메일 수 차이"가 보이도록 캡처 또는 삭제된 메일함(휴지통) 스크린샷

    예시:받은편지함 1423개 → 1261개',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/12.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/12.jpeg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (13,
        3,
        '채식 식사 인증',
        '1일 1끼 이상 채식 식사를 실천해 지속 가능한 식습관을 만드는 챌린지입니다.',
        '육류·유제품 중심의 식사 → 채소/식물성 위주의 식사로 바꾸는 것만으로도 우리는 탄소배출, 물 소비, 삼림 훼손을 실질적으로 줄일 수 있습니다.
   이 챌린지는 1일 1끼 이상 비건 또는 채식 식사를 실천하고 인증함으로써, 지속 가능한 식습관을 만들고 지구에 긍정적인 영향을 주는 것을 목표로 합니다.

   하루 중 1끼 이상 고기/어류/유제품이 포함되지 않은 식사를 선택해 드세요
   • 비건: 동물성 성분 완전 제외
   • 채식: 육류 제외, 일부 유제품/계란 포함 가능직접 조리한 식사 or 비건 식당/제품 사용 모두 OK식사 사진을 촬영하고 간단한 식단 설명과 함께 인증',
        ' 식사 사진 + 채식/비건 여부가 드러나는 사진

   옵션: 비건 제품 라벨도 함께 인증 (ex. 비건 인증 마크, 성분표시, 비건 식당 등)',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/13.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/13.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (14,
        3,
        '유통기한 임박 식품 소비 인증',
        '유통기한 임박 식품을 소비해 음식물 쓰레기를 줄이는 챌린지입니다.',
        ' ‘유통기한이 임박했다는 이유로’ 버려지는 음식이 전 세계에서 매년 10억 톤 이상입니다.
   이 챌린지는 마트, 편의점, 냉장고 속에서 유통기한이 임박한 식품을 찾아 낭비 없이 소비하고 인증하는 것을 목표로 합니다.
   유통기한이 임박한 제품을 소비하는 것만으로도 음식물 쓰레기를 줄이고, 탄소와 수자원 낭비를 크게 줄일 수 있어요.',
        '마트, 편의점, 혹은 가정 내에서

   **유통기한이 임박한 식품(예: D-1~3일 이내)**을 찾아 구매하거나 소비하세요해당 제품의 포장지에 표시된 유통기한이 보이도록 촬영가능하다면 해당 식품을 조리하거나 먹은 사진도 함께 인증선택적으로 “어떻게 활용했는지” 간단한 후기를 작성해주세요

   제품 라벨에 유통기한이 보이는 사진 + 식품 실물 or 조리된 사진',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/14.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/14.png',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (15,
        4,
        '도보 15분 이상 실천하기',
        '하루 15분 이상 걷기로 자동차 사용을 줄이고 탄소 배출을 낮추는 챌린지입니다.',
        '자동차를 이용하지 않고 걷는 습관을 생활화한다면 개인의 연간 탄소 배출량을 약 0.5톤 줄일 수 있습니다.
   일반 승용차는 매년 약 4.6톤의 이산화탄소(CO₂)를 배출하며, 이는 환경에 막대한 영향을 미칩니다.
   도보 15분 이상 실천하기 챌린지는 건강을 증진시키는 동시에 자동차 의존도를 줄여 지속 가능한 생활 방식을 장려합니다.',
        '• 도보 중 찍은 사진(예: 공원, 거리, 산책로 등)
   • 걸음 수를 확인할 수 있는 앱 스크린샷 또는 스마트워치 기록',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/15.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/15.png',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (16,
        4,
        '친환경적인 이동',
        '자전거나 전동 킥보드 등의 이동수단을 이용해 탄소 배출을 줄이는 챌린지입니다.',
        '교통 부문은 전 세계 온실가스 배출량의 약 14%를 차지하며, 개인 자동차 사용이 주요 원인 중 하나입니다.
   개인 자전거 또는 공유 전동 킥보드와 같은 친환경적인 이동 수단을 선택하여 1.5km를 이동하면 약 312g의 CO₂ 배출을 줄일 수 있고, 이는 휴대폰을 약 100시간 충전하는 동안 발생하는 탄소량과 비슷합니다.
   이 챌린지는 환경 보호와 지속 가능성을 실천하며 개인의 경제적 비용 절감에도 기여합니다.',
        '• 자전거 또는 전동 킥보드 이용 중 사진
   • 공유 킥보드, 자전거를 이용한 기록이나 결제 내역 앱 스크린샷',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/16.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/16.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (17,
        4,
        '계단 오르기',
        '엘리베이터 대신 계단을 이용해 에너지를 아끼고 건강을 챙기는 챌린지입니다.',
        '엘리베이터 대신 계단을 이용하면 에너지를 절약하고 건강을 증진시킬 수 있습니다.
   예를 들어, 5층을 엘리베이터 대신 계단으로 오르면 이 전력을 절약할 수 있으며, 이는 스마트폰을 약 10회 충전할 수 있는 에너지에 해당합니다.
   특히 계단 한 층을 오를 때는 약 7kcal를 소모하고, 하루에 한 번씩 5층 정도를 계단으로 오르면 연간 3kg의 체중 감량 효과를 얻을 수 있습니다.',
        '• 계단을 오르는 모습을 찍은 사진
   • 계단 층수를 기록한 앱 스크린샷 또는 기록표 사진
   • 고도 상승, 하강 등의 지표가 담긴 운동 앱 내 기록 스크린샷',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/17.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/17.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (18,
        4,
        '대중교통 이용하기',
        '자동차 대신 대중교통을 이용해 탄소 배출을 줄이고 비용도 절약하는 챌린지입니다.',
        '자동차 대신 대중교통을 이용하면 연간 약 4.6톤의 CO₂ 배출을 줄일 수 있습니다.
   자동차 대신 대중교통으로 5km를 이동하면 약 1.91~2.01kg의 CO₂ 배출을 줄일 수 있습니다.
   이는 휴대폰을 약 60~80시간 동안 충전하는 동안 발생하는 탄소량과 비슷합니다.
   대중교통 이용하기 챌린지를 통해 자동차의 기름값도 아끼고 탄소 배출량도 줄여봐요!',
        '• 대중교통 티켓 또는 카드 사용 내역 사진
   • 대중교통 이용 중 찍은 풍경 또는 내부 사진',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/18.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/18.webp',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL),
       (19,
        4,
        '플로깅 실천하기',
        '조깅·산책하며 쓰레기를 줍는 플로깅으로 탄소와 지역 환경을 지키는 챌린지입니다.',
        '조깅이나 산책 중 쓰레기를 줍는 플로깅 활동은 매립지에서 발생하는 메탄가스를 줄이고 지역 환경을 깨끗하게 유지하는 데 기여합니다.
음식물 쓰레기만 해도 전 세계 온실가스 배출량의 8~10%를 차지하고, 하루에 100g의 쓰레기를 수거하면 약 15g의 CO₂ 배출을 줄이는 효과를 얻습니다.
이는 LED 전구를 약 5시간 동안 사용하는 동안 발생하는 탄소량과 비슷합니다.
작은 행동이 지구에 큰 변화를 가져올 수 있습니다.',
        '• 플로깅 중 주운 쓰레기와 함께 찍은 사진(예: 쓰레기봉투)
• 플로깅 활동 중 찍은 풍경 또는 장소 사진',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/challenge_main/19.png',
        'https://storage.googleapis.com/dgu-gdg-greenap-cloud-storage/challenges/certificate_example/19.jpg',
        '2025-04-21 00:00:00', '2025-04-21 00:00:00', NULL);


INSERT
IGNORE INTO users (user_id, nickname, email, profile_image_url, now_max_consecutive_participation_day_count,
                   refresh_token, created_at, updated_at, deleted_at)
VALUES (1,
        'testUser',
        'testEmail@email.com',
        'testImageUrl',
        0,
        'testRefreshToken',
        '2025-04-21 00:00:00',
        '2025-04-21 00:00:00',
        null),
       (2,
        'testUser2',
        'test2Email@email.com',
        'test2ImageUrl',
        0,
        'test2RefreshToken',
        '2025-04-21 00:00:00',
        '2025-04-21 00:00:00',
        null)
