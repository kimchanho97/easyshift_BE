# 이지시프트(EasyShift)

> 근무 일정 관리와 자동 배정을 지원하는 스마트 스케줄링 플랫폼

### 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [팀원 소개](#%EF%B8%8F-팀원-소개)
3. [스케줄링 알고리즘](#-스케줄링-알고리즘)
4. [트러블 슈팅](#-트러블-슈팅)
5. [화면 구성](#-화면-구성)
6. [시작 가이드](#%EF%B8%8F-시작-가이드)
7. [ERD](#%EF%B8%8F-erd)

<br>

## 🚀 프로젝트 소개

### 개발 동기 및 목적

매월 반복되는 교대 근무 스케줄을 작성하는 과정은 많은 시간과 노력이 필요합니다. 관리자는 근무 배치와 휴무 반영, 변경 요청 처리까지 많은 요소를 고려해야 합니다. 반면, 근로자는 근무표 확인이 어렵고, 변경
요청이 즉시 반영되지 않는 불편함을 겪습니다.

📌 **이지시프트(EasyShift)는 이러한 문제를 해결하기 위해 개발되었습니다.**

- 자동 스케줄링 기능을 제공하여 근무 인원 배치를 효율적으로 수행하고,
- 근무 변경 요청 시스템을 통해 실시간으로 스케줄을 조정하며,
- 직관적인 근무 일정 확인 기능으로 모든 직원이 자신의 근무 상태를 쉽게 확인할 수 있도록 합니다.

이를 통해 관리자는 업무 부담을 줄이고, 근로자는 보다 원활하게 근무 일정을 관리할 수 있도록 돕는 것이 이지시프트의 목표입니다.

<br> 

### 서비스 소개

이지시프트는 자동화된 스케줄링과 실시간 근무 조정 기능을 제공하여 매장의 업무 부담을 줄이고, 근로자의 근무 일정 관리를 더욱 원활하게 만듭니다.

**주요 특징**

- ✅ **자동 스케줄링** - 직원 휴무 신청을 기반으로 자동 배정
- ✅ **실시간 근무 조정** - 변경 요청 즉시 반영 (관리자 승인 필요)
- ✅ **직관적인 일정 확인** - 편리한 UI에서 최신 근무 스케줄을 실시간으로 확인
- ✅ **초대 링크 시스템** - 간단한 초대로 매장에 직원 추가

이 모든 기능을 통해 직관적이고 효율적인 근무 관리 환경을 제공합니다.

<br> 

### 개발 기간

- 2025.02.17 ~ 2025.03.16 (구름톤 DEEP DIVE 3단계 팀프로젝트)

<br> 

### 주요 기능

이지시프트는 관리자와 근무자의 역할에 맞춘 맞춤형 기능을 제공합니다.

✅ **관리자(Admin)**

- **매장 관리**: 매장 생성 및 직원 초대 (초대 링크 자동 생성)
- **근무 일정 관리**: 스케줄 자동 생성 및 조정 (직원 신청 기반 자동 배치)
- **근무 요청 관리**: 직원의 근무 요청 및 휴무 신청 승인/거절

✅ **근무자(Worker)**

- **근무 스케줄 확인**: 본인의 근무 일정 조회
- **근무 신청 및 변경 요청**: 희망 근무 일정 신청 및 조정 요청
- **휴무 신청**: 휴가 및 휴무 신청 (관리자 승인 필요)

<br>

## 🙋🏻‍♂️ 팀원 소개

|                             BE                             |                             BE                              |                          BE                          |                            BE                             |                          FE                          |                           FE                           |                            INFRA                            | 
|:----------------------------------------------------------:|:-----------------------------------------------------------:|:----------------------------------------------------:|:---------------------------------------------------------:|:----------------------------------------------------:|:------------------------------------------------------:|:-----------------------------------------------------------:|
| <img src="https://github.com/kimchanho97.png" width="100"> | <img src="https://github.com/YeongJae0114.png" width="100"> | <img src="https://github.com/s1uth.png" width="100"> | <img src="https://github.com/26solitude.png" width="100"> | <img src="https://github.com/Xoeon.png" width="100"> | <img src="https://github.com/yuunhae.png" width="100"> | <img src="https://github.com/IamGroooooot.png" width="100"> |
|           [김찬호](https://github.com/kimchanho97)            |           [이영재](https://github.com/YeongJae0114)            |           [손태인](https://github.com/s1uth)            |           [조장호](https://github.com/26solitude)            |           [양소연](https://github.com/Xoeon)            |           [조윤해](https://github.com/yuunhae)            |           [고주형](https://github.com/IamGroooooot)            |

<br> 

### 나의 역할

1️⃣ **API 설계 & ERD 최적화**

- REST API 설계: API 엔드포인트 구조 및 UX 관점에서의 API 설계
- ERD 및 테이블 설계: 실제 스케줄 데이터 변경 문제 해결을 위한 ERD 개선 및 최적화

2️⃣ **백엔드 구조 및 코드 컨벤션 정리**

- 폴더 구조 설계: 도메인 중심 설계 적용 및 역할별 계층 구분
- DTO ↔ Entity 변환 로직 정의: `toEntity()`, `fromEntity()` 패턴 팀원 공유
- 예외 처리 구조 설계: 예외 계층 구조 정의 및 적용
- API 응답 표준화: `ApiResponse` 설계 및 팀 내 공유

3️⃣ **스케줄링 알고리즘 구현 & 성능 최적화**

- 근무 스케줄 자동 배정 알고리즘 설계 및 구현
- 자동 배정 과정의 성능 최적화 (변경 감지 → 배치 처리 개선)

📁 [프로젝트 개발 문서 바로가기(Notion)](https://kimchanho.notion.site/1b7a1b1b004180fd82abee68dcf58917?pvs=4)

<br>

## 📅 스케줄링 알고리즘

### 🤖 자동 근무 배정 알고리즘

이지시프트의 스케줄링 알고리즘은 **모든 유저에게 균등한 근무를 배정하고, 휴무 신청을 반영하며, 연속 근무를 최소화하는 것**을 목표로 합니다. 이를 위해 **그리디(Greedy) 기법과 라운드 로빈(Round
Robin) 방식을 결합한 알고리즘**을 사용합니다.

- **그리디(Greedy)**: 현재 가능한 최적의 유저를 선택하여 근무를 배정
- **라운드 로빈(Round Robin)**: 모든 유저가 순환하면서 근무를 배정받아 균등한 배정을 보장

📌 **핵심 요구사항**

1. *모든 유저가 동일한 개수의 근무를 배정받도록 보장*
2. *동일한 시간대에 같은 유저가 중복 배정되지 않도록 처리*
3. *휴무 신청을 최우선으로 반영하고, 근무 로테이션 텀을 유지*

<img width="700" alt="Image" src="https://github.com/user-attachments/assets/36db9f8f-668a-483a-9eb5-a58b978a87a8" />

📂 [상세 알고리즘 설명 보기(Notion)](https://kimchanho.notion.site/1bba1b1b004180d49f2be9af805d81a4)

<br>

### 🔥 개별 UPDATE → Batch Update로 성능 5배 개선하기

초기에는 자동 배정 시 **JPA 변경 감지(Dirty Checking)로 인해 근무(Shift) 개수만큼 개별 UPDATE 쿼리가 발생하는 문제**가 있었습니다.
예를 들어, 한 달 동안 할당해야 할 근무 개수가 900개라면, 900개의 UPDATE 쿼리가 개별적으로 실행됩니다.

이를 해결하기 위해, **JdbcTemplate의 `batchUpdate()`** 를 활용하여 유저 배정 정보를 `(user_id, shift_id)` 쌍으로 모은 뒤,
한 번의 배치 쿼리로 일괄 처리하는 구조로 리팩토링했습니다.

- ✅ **Batch Update 적용 후, 자동 배정 성능이 5배 이상 개선됨**
- ✅ **900개 근무에 단 1번의 쿼리로 처리 → 네트워크/DB 부담 최소화**

📂 [성능 최적화 과정 보기(Notion)](https://kimchanho.notion.site/DEEP-DIVE-1bba1b1b00418033a387fab64473a941)

<br>

## 🔫 트러블 슈팅

### 📌 스케줄 템플릿 수정 시 실제 운영 스케줄 보호하기

스케줄 생성 시 `ScheduleTemplate`을 참조하도록 설계된 초기 구조는,  
**템플릿 수정/삭제 시 실제 운영 데이터까지 변경되는 문제**를 유발했습니다.

- 스케줄 생성 시점에 템플릿 정보를 **복사하여 저장하는 구조로 리팩토링**
- `(FK 참조 → 값 복사 + 참조 ID만 남김)` 형태로 **역정규화 전략 적용**
- 템플릿 변경이 운영 데이터에 영향을 주지 않으며, 조회 시 템플릿 기준 필터링도 가능
- **쿼리 성능 개선 + 과거 데이터 정합성 보장 + UX 기반 필터링 요구사항까지 반영**

설계 유연성과 데이터 보호를 동시에 만족하는 구조로 개선하였으며,  
**템플릿 기반 스케줄 관리 UX 흐름에 최적화된 테이블 모델링을 달성했습니다.**

📂 [문제 분석과 테이블 리팩토링 과정 보기(Notion)](https://kimchanho.notion.site/1bba1b1b00418097b82cd83fa1e99574)

<br>

### 🔄 자동 근무 배정 알고리즘 개선: FIFO 큐 → 우선순위 큐

자동 근무 배정 로직에서 **라운드 로빈 기반의 균등 배정**을 유지하면서도,  
**휴무 신청 반영**과 **연속 근무 방지**를 충족시키기 위해 알고리즘을 리팩토링했습니다.

- **기존 방식**: `FIFO 큐` 기반 → 휴무 유저는 단순히 뒤로 밀려 **배정 순서가 무너짐**
- **개선 방식**: `우선순위 큐 + 대기 풀(waiting pool)` 도입

    - 초기 인덱스를 기준으로 우선순위 유지
    - 조건 미충족 유저는 `waiting pool`로 이동
    - 배정 완료 후 `waiting pool`을 복구하여 **공정한 순환 구조 유지**

📂 [문제 정의 및 개선 과정 보기(Notion)](https://kimchanho.notion.site/1bba1b1b004180d49f2be9af805d81a4)  
📂 [전체 코드 보기(GitHub)](https://github.com/kimchanho97/easyshift_BE/blob/main/src/main/java/com/burntoburn/easyshift/scheduler/AutoAssignmentScheduler.java)

<br>

## 📷 화면 구성

|       **이름**       |                                                        **이미지**                                                        |
|:------------------:|:---------------------------------------------------------------------------------------------------------------------:|
|     **메인 페이지**     | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/96517f43-a320-4025-b5e5-899523e6dcbd" /> |
|     **설정 페이지**     | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/b3573b12-62ae-4779-8310-2355b7b6e67f" /> |
|   **스케줄 템플릿 등록**   | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/33e2d63f-f674-43b0-b101-5acf194b14d7" /> |
|     **스케줄 등록**     | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/94525081-8ec1-4824-bb6d-7f8209a57cc1" /> | 
|     **휴무 신청**      | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/573194d1-2996-4711-9793-028343bba63a" /> | 
|   **스케줄 자동 배정**    | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/6e2ddb15-3edb-4b5c-b394-a26f82f27a92" /> | 
|   **스케줄 확인 페이지**   | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/71133c81-8fd4-4774-b7c2-47d6ed014ab9" /> | 
|   **나의 스케줄 확인**    | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/13895646-b7bd-43d3-86f6-17ab31a71a20" /> | 
|     **근무 교환**      | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/3db3d6f1-a0a3-488a-a3c0-fe56fa8b2fe1" /> | 
| **근로자의 월별 스케줄 확인** | <img width="700" alt="Image" src="https://github.com/user-attachments/assets/6dafb05e-a1f4-4b3f-8325-df3b420f9ba1" /> | 

<br>

## ⚙️ 시작 가이드

### 기술 스택

- **Framework**: Spring Boot 3.4.2
- **Language**: Java 21
- **Database**: MySQL, H2 (테스트용)
- **Security**: Spring Security, OAuth2 (카카오 로그인), JWT
- **Persistence**: Spring Data JPA
- **Monitoring**: Spring Boot Actuator, Micrometer (Prometheus 연동)
- **Benchmarking**: JMH (Java Microbenchmark Harness)
- **API Documentation**: SpringDoc OpenAPI (Swagger)

<br>

### 프로젝트 빌드 및 실행

```bash
git clone https://github.com/your-repo/easyshift.git
cd easyshift/backend
./gradlew clean build
java -jar build/libs/easyshift-0.0.1-SNAPSHOT.jar
```

<br> 

## 🗺️ ERD

<img alt="Image" src="https://github.com/user-attachments/assets/8c70313d-c7ae-4ee1-9dab-41b3bcda0921" />