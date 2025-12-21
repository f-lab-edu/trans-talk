# 💬 TransTalk 💬

국경에 관계없이 모국어/도착언어를 고려한 자동 번역 채팅 환경을 제공하는 서비스입니다. 사용자 사이의 무작위 매칭이 이루어지며, 각 사용자는 무작위로 매칭된 상대방의 프로필을 확인한 후 자유롭게 채팅방을 열어 대화를 수행할 수 있습니다.

---


## 작업 기록

다음은 주요 항목별 작업 내용입니다.

- 번역 기능
    - [**[#11] 번역 서비스 로직 구현 및 채팅 서비스와 연계**](https://github.com/f-lab-edu/trans-talk/pull/24)
    - [**[#26] OpenAI provider 추가 및 provider 간의 실행 우선순위 구조화**](https://github.com/f-lab-edu/trans-talk/pull/27)
- 소셜 로그인
    - [**[#6] OAuth2 소셜 로그인, 회원가입 및 JWT 인증 구현**](https://github.com/f-lab-edu/trans-talk/pull/7)
    - [**[#8] 회원가입 절차 리팩토링 및 사용자 정의 예외 추가**](https://github.com/f-lab-edu/trans-talk/pull/15)
    - [**[#20] 로그인 정보 기반하도록 api 수정**](https://github.com/f-lab-edu/trans-talk/pull/21)
- 프로필 이미지 관리
    - [**[#2] 포스트와 함께 구성된 프로필 구현**](https://github.com/f-lab-edu/trans-talk/pull/5)
    - [**[#9, #16] CloudFront + Signed Cookie 전환 및 이미지 처리 로직 구체화**](https://github.com/f-lab-edu/trans-talk/pull/17)
    - [**[#22] CloudFront Signed Cookies 연동 개선**](https://github.com/f-lab-edu/trans-talk/pull/23)
- 웹소켓 기반 채팅 환경 구성
    - [**[#10] 웹소켓을 사용한 실시간 채팅 기능 구현**](https://github.com/f-lab-edu/trans-talk/pull/18)


## 주요 관심사

- 이슈/PR 구조를 적극 활용하여 해결하고자 하는 작업 대상과 해결 과정을 담은 작업 내용을 틈틈이 정리하였습니다.
- YAGNI 원칙에 근거하여 코드를 관리하였습니다.
- 객체지향적 개념에 기반하여 코드 상의 응집도와 결합도를 고려한 설계를 지향했습니다.
- Spring Boot의 DI 컨테이너에 기반한 의존성 제어를 적극 활용하였습니다.
- 사용 기술을 정확히 이해하고 활용할 수 있도록 필요하다면 구현 코드의 동작 흐름을 시퀀스 다이어그램 등으로 표현하였습니다.


## 프로젝트 DB ERD

<img width="997" height="693" alt="db erd" src="https://github.com/user-attachments/assets/a7b74c71-89d2-4dc5-ba7c-a53c56eb6e29" />

