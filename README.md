# 학생 식당 개인 사용 관리 앱

> 2026년 모바일 소프트웨어 학기말 프로젝트 · Team 6  
> Kotlin · Jetpack Compose · Room Database

학생 식당 이용자가 식사를 기록하고 섭취량에 따른 영양소와 칼로리를 확인하는 Android 애플리케이션입니다.

## 1. 프로젝트 소개

음식과 반찬, 식사 날짜·시간, 사진, 섭취량을 기록하고 탄수화물·단백질·지방·칼로리를 계산합니다. 저장된 식사는 목록과 상세 화면에서 조회하며, 날짜별 요약 및 최근 30일 분석으로 식사 현황을 확인할 수 있습니다.

| 항목 | 내용 |
|---|---|
| 프로젝트 | 학생 식당 개인 사용 관리 앱 |
| 팀원 | 박준서 · 박지환 |
| 플랫폼 | Android |
| 저장소 | https://github.com/junepark12/mobile-software-term-project_team-6 |
| 시연 영상 | 업로드 후 YouTube 링크 추가 예정 |

## 2. 주요 기능

| 기능 | 상세 내용 |
|---|---|
| 음식 입력 | 음식 검색·선택, 여러 음식과 반찬 추가, 섭취량(g) 입력 |
| 식사 정보 | 날짜·시간 선택, 조식·중식·석식·간식/음료 구분 |
| 사진 등록 | 사진 선택, 미리보기 및 식사 기록과 이미지 URI 연결 |
| 영양 미리보기 | 섭취량에 따른 탄수화물·단백질·지방·칼로리 계산 |
| 식사 저장 | Room을 이용한 식사와 세부 음식 항목 저장 |
| 목록 및 상세 | 최신순 목록, 날짜별 그룹화, 사진·음식·섭취량·영양정보 조회 및 삭제 |
| 날짜별 요약 | 날짜별 총칼로리와 식사 기록 수 표시 |
| 최근 30일 분석 | 총칼로리, 하루 평균, 날짜별 칼로리 추이 및 영양소 합계 |
| 끼니별 분석 | 조식·중식·석식·간식/음료별 칼로리 비율과 영양 통계 |
| 출처 안내 | 앱 내 출처/정보 화면에서 영양 데이터 출처 표시 |

### 계산 및 집계 기준

- 음식 영양정보는 100g 기준으로 관리합니다.
- 실제 섭취 영양값 = 100g 기준 영양값 × 섭취량(g) ÷ 100
- 여러 음식이 포함된 식사는 각 음식의 환산 영양값을 합산합니다.
- 끼니 수는 음식 개수가 아닌 저장된 식사 기록 수를 기준으로 합니다.
- 월간 분석은 오늘부터 29일 전까지, 양 끝 날짜를 포함한 30일을 기준으로 합니다.
- 기록이 없는 날짜의 칼로리는 0으로 표시하며, 30일 하루 평균은 해당 기간 총칼로리를 30으로 나눕니다.

## 3. 개발 환경 및 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 개발 환경 | Android Studio |
| 언어 | Kotlin (Compose 플러그인 2.2.10) |
| Android SDK | compileSdk 37 · targetSdk 37 · minSdk 28 |
| 빌드 설정 | AGP 9.3.3 · Gradle Wrapper 9.5.0 |
| Java 컴파일 타깃 | Java 11 (Gradle 실행 JDK와 별도) |
| UI | Jetpack Compose, Material 3 |
| 화면 이동 | Navigation Compose |
| 구조 | MVVM 및 Repository 패턴 |
| 상태 및 비동기 처리 | ViewModel, Kotlin Coroutines, Flow |
| 로컬 저장 | Room Database, KSP |
| 이미지 로딩 | Coil Compose |
| 단위 테스트 | JUnit4 |
| UI 테스트 | AndroidX Compose UI Testing |
| AI 개발 도구 | Android Studio 내부 Gemini Agent |
| 기기 테스트 환경 | Pixel 9 에뮬레이터 |

라이브러리 버전은 Gradle 설정과 `gradle/libs.versions.toml`에서 확인할 수 있습니다.

## 4. 설치 및 실행 방법

### 4.1. 프로젝트 다운로드

Windows PowerShell에서 다음 명령을 실행합니다.

~~~powershell
git clone https://github.com/junepark12/mobile-software-term-project_team-6.git
cd mobile-software-term-project_team-6
~~~

Git을 사용하지 않는 경우 GitHub의 **Code → Download ZIP**으로 내려받아 압축을 해제합니다.

### 4.2. Android Studio 실행

1. Android Studio에서 프로젝트 루트 폴더를 엽니다.
2. Gradle Sync가 완료될 때까지 기다립니다.
3. SDK Manager에서 Android SDK Platform 37을 설치합니다.
4. Android Studio의 Gradle JDK를 프로젝트의 AGP·Gradle 버전에 호환되는 JDK로 설정합니다. Java 컴파일 타깃 11과 Gradle 실행 JDK 버전은 별개입니다.
5. Android 9.0(API 28) 이상의 에뮬레이터 또는 USB 디버깅이 활성화된 기기를 준비합니다.
6. 실행 구성을 `app`으로 선택하고 Run 버튼을 누릅니다.

### 4.3. Debug APK 빌드

프로젝트 루트에서 실행합니다.

~~~powershell
.\gradlew.bat :app:assembleDebug
~~~

생성 위치: `app/build/outputs/apk/debug/app-debug.apk`

## 5. 데이터베이스 구조

Room 데이터베이스명은 `cafeteria_meal_db`입니다.

| 테이블 | 주요 필드 | 역할 |
|---|---|---|
| `foods` | foodId, foodCode, foodName, category, carbohydratePer100g, proteinPer100g, fatPer100g, caloriePer100g, dataSource, isVerified | 음식 기준 영양정보 |
| `meals` | mealId, mealDate, mealTime, mealType, imageUri, createdAt | 한 번의 식사 기록 |
| `meal_items` | mealItemId, mealId, foodId, foodName, quantityGram, carbohydratePer100g, proteinPer100g, fatPer100g, caloriePer100g | 식사에 포함된 음식과 섭취량 |

- 하나의 식사는 여러 음식 항목을 가집니다.
- 식사 삭제 시 연결된 음식 항목은 CASCADE 방식으로 삭제됩니다.
- 음식 기준 데이터 삭제 시 연결된 foodId는 SET_NULL 방식으로 처리합니다.
- 식사와 음식 항목은 트랜잭션으로 저장해 부분 저장을 방지합니다.
- 사진은 이미지 자체 대신 URI를 저장합니다.

## 6. 영양 데이터 출처

| 항목 | 내용 |
|---|---|
| 데이터에 기재된 출처 | 식품의약품안전처 공공데이터 (`dataSource` 필드) |
| 공공데이터 안내 사이트 | https://www.data.go.kr |
| 자료 | 음식별 100g 기준 영양성분 데이터 |
| 앱 내 데이터 파일 | `app/src/main/assets/foods.json` |
| 데이터 구성 | 100개 음식, 10개 카테고리 |
| 기준 단위 | 음식 100g당 탄수화물·단백질·지방(g), 열량(kcal) |
| 앱 내 표시 | 출처/정보 화면(`InfoScreen`) |

음식 데이터는 앱에 포함된 JSON을 Room으로 읽어 사용합니다. 앱 실행 중 영양정보 API를 호출하지 않으므로 별도의 API 키 입력은 필요하지 않습니다.

`foods.json`에는 음식별 출처가 `식품의약품안전처 공공데이터`로 기재되어 있습니다. 원본 데이터셋의 상세 주소와 버전·수집일은 저장소에 기록되어 있지 않아 확인이 필요합니다. 위 안내 사이트는 공공데이터포털의 주소이며, 실제 사용한 원본 데이터셋의 상세 링크는 아닙니다.

포함된 카테고리는 밥류, 국·찌개류, 고기류, 생선류, 채소류, 반찬류, 면류, 과일류, 간식류, 음료류입니다.

## 7. 팀원 역할 분담

| 팀원 | 담당 영역 | 세부 담당 범위 |
|---|---|---|
| 박준서 (개발자 A) | 데이터 및 핵심 계산 로직 | 데이터 구조 설계, Room Entity·DAO·Database와 Repository 구성, 공공 음식 데이터 정리, 섭취량별 영양 계산, 날짜별·월간 집계, 핵심 로직 단위 테스트 |
| 박지환 (개발자 B) | UI 및 사용자 흐름 | Compose 디자인 시스템, Navigation, 검색·섭취량·날짜·시간 입력 UI, 사진 선택·미리보기, 목록·상세 화면, 분석 시각화, Compose UI 테스트 작성 |
| 공동 | 통합 및 제출 | 데이터 모델과 화면 연결 규격 확인, 요구사항 검수, 통합 테스트, README와 AI 활용 내역 정리, 시연 영상 및 제출 링크 점검 |

## 8. 팀원별 Vibe Coding 활용 내역

### 8.1. 개발 도구

Android Studio의 Gemini Agent를 활용해 담당 기능의 코드 작성과 오류 수정을 진행했습니다. 데이터·계산 로직과 화면 구현을 나누고, 각 기능을 연결한 뒤 빌드와 테스트로 동작을 확인했습니다.

아래 D 번호는 개인별 PDF의 PROMPT 번호입니다. 예를 들어 D01은 PROMPT 01을 뜻합니다.

| 팀원 | 프롬프트 본문 | 수록 항목 |
|---|---|---|
| 박준서 | [박준서 프롬프트 PDF](docs/박준서_프롬포트.pdf) | 개인 담당 11개 + 공통 3개 |
| 박지환 | [박지환 프롬프트 PDF](docs/박지환_프롬포트.pdf) | 개인 담당 11개 + 공통 3개 |

공통 프롬프트 01·24·25는 두 파일에 모두 포함되어 있습니다.

프롬프트는 개발 당시의 요청 내용이고, 최종 구현 범위는 2절을 기준으로 합니다. 최근 1개월 분석은 코드와 테스트에서 오늘을 포함한 30일로 통일했습니다. 기존 식사 수정 화면은 제공하지 않으며, 저장된 기록은 조회·삭제할 수 있습니다.

### 8.2. 박준서 (개발자 A) — 데이터 및 계산 로직

| 프롬프트 | 작업 | 활용 내용 |
|---|---|---|
| D02 | 프로젝트 폴더 구조 | MVVM과 Repository 패턴에 따른 계층별 책임 분리 |
| D03 | Gradle 환경 설정 | Room·KSP·ViewModel·테스트 의존성과 빌드 설정 점검 |
| D04 | Room 데이터베이스 | Food·Meal·MealItem 관계, DAO와 Database 구성 |
| D05 | 공공 영양 데이터 | 음식 데이터의 기준량·단위·출처 확인 |
| D06 | 음식 Repository | 초기 등록, 중복 방지, 검색·카테고리·상세 조회 |
| D07 | 영양 계산 | 섭취량 환산, 음식 합산 및 잘못된 입력 검증 |
| D15 | 식사 저장 | 여러 음식 저장, 필수값 검증 및 트랜잭션 처리 |
| D18 | 날짜별 요약 | 날짜별 영양소·칼로리 및 식사 기록 수 집계 |
| D19 | 최근 1개월 분석 | 기간 필터링, 총칼로리·평균·날짜별 집계 |
| D20 | 끼니별 분석 | 끼니 유형별 영양소와 칼로리 집계 |
| D22 | 로컬 단위 테스트 | 영양 계산, 날짜별 요약, 월간 분석 및 검색 검증 |

주요 관련 구성: Room Entity·DAO·Database, Repository, `foods.json`, `NutrientCalculator`, `DailySummaryCalculator`, `MealAnalysisCalculator`, 로컬 단위 테스트.

### 8.3. 박지환 (개발자 B) — UI 및 사용자 흐름

| 프롬프트 | 작업 | 활용 내용 |
|---|---|---|
| D08 | 디자인 시스템 | 색상·테마·타이포그래피 및 공통 UI 구성 |
| D09 | Navigation | 주요 화면 연결, 하단 내비게이션 및 식사 ID 전달 |
| D10 | 메인 화면 | 오늘의 영양정보·끼니 수, 오늘 식사 목록 및 추가 동선 |
| D11 | 음식 검색 | 검색어 입력, 검색 결과 표시 및 음식 선택 |
| D12 | 섭취량·미리보기 | 입력 상태 관리 및 실시간 영양 계산 결과 표시 |
| D13 | 날짜·시간·끼니 | 날짜·시간 선택 및 끼니 종류 입력 |
| D14 | 사진 등록 | Photo Picker, 미리보기·변경 및 URI 접근 유지 검토 |
| D16 | 목록 화면 | 식사 카드, 날짜별 그룹화, 빈 상태 및 상세 이동 |
| D17 | 상세 화면 | 사진·음식·섭취량·영양정보 및 삭제 동선 |
| D21 | 분석 UI 통합 | 날짜별 추이 및 끼니별 분석 결과를 차트·카드로 표현 |
| D23 | Compose UI 테스트 | 입력·목록 화면의 주요 UI 및 상태 표시 검증 |

주요 관련 구성: `Color.kt`, `Theme.kt`, `Type.kt`, Navigation, `HomeScreen`, `MealInputScreen`, `FoodSearchScreen`, `MealListScreen`, `MealDetailScreen`, `AnalysisScreen`, `MonthlyCalorieChart`, `MealTypeDonutChart`, `MealInputUiTest`, `MealListUiTest`.

### 8.4. 공동 프롬프트 및 기능 연계

| 번호 | 주제 | 협업 범위 |
|---|---|---|
| D01 | 전체 요구사항 | 필수 기능·데이터·테스트·제출 조건 확인 |
| D24 | 최종 검수 | 전체 기능, README, 테스트 결과 및 시연 항목 확인 |
| D25 | 오류 진단 | 관련 로그와 파일 확인, 최소 수정 및 수정 후 검증 |

- D15: 박준서는 저장·트랜잭션, 박지환은 저장 버튼·검증 메시지·저장 후 화면 이동을 담당합니다.
- D18: 박준서는 집계 로직, 박지환은 날짜별 요약 표시를 담당합니다.
- D19~D20: 박준서는 통계 계산, 박지환은 D21을 통한 차트·분석 화면 구성을 담당합니다.

## 9. 테스트 실행 방법 및 결과

로컬 단위 테스트 17개와 Pixel 9 에뮬레이터 기기 테스트 4개가 모두 통과했습니다.

### 9.1. 최종 결과

| 구분 | 전체 | 성공 | 실패 | 건너뜀 |
|---|---:|---:|---:|---:|
| 로컬 단위 테스트 | 17 | 17 | 0 | 0 |
| Pixel 9 기기 테스트 | 4 | 4 | 0 | 0 |

- 핵심 로직 단위 테스트 **16개** + 기본 샘플 **1개**
- Compose UI 테스트 **3개** + 기본 기기 테스트 **1개**
- 핵심 로직 12개 이상, Compose UI 2개 이상이라는 요구 개수를 충족합니다.

### 9.2. 단위 테스트 상세

| 테스트 클래스 | 검증 대상 | 통과 수 |
|---|---|---:|
| `NutrientCalculatorTest` | 섭취량별 영양 계산, 합산 및 예외 입력 | 9 |
| `DailySummaryCalculatorTest` | 날짜별 그룹화와 집계 | 3 |
| `MealAnalysisCalculatorTest` | 월간 분석 로직 | 2 |
| `FoodSearchLogicTest` | 음식 검색 및 필터링 | 2 |
| `ExampleUnitTest` | 기본 샘플 | 1 |
| **합계** | 핵심 로직 16개 + 샘플 1개 | **17** |

### 9.3. 기기 테스트 상세

| 구분 | 관련 테스트 | 통과 수 |
|---|---|---:|
| 식사 입력 UI | `MealInputUiTest`: 주요 입력 요소와 버튼 표시 | 1 |
| 식사 목록 UI | `MealListUiTest`: 빈 목록 및 준비된 식사 데이터 표시 | 2 |
| 기본 기기 테스트 | `ExampleInstrumentedTest`: 앱 패키지명 확인 | 1 |
| **합계** | Pixel 9 에뮬레이터 | **4** |

UI 테스트 3개는 화면 요소와 목록 상태 표시를 검증합니다. 사진 선택·실제 저장·상세 이동을 포함한 전체 흐름은 시연 영상에서 확인할 수 있도록 구성합니다.

### 9.4. 실행 명령

프로젝트 루트의 Windows PowerShell에서 실행합니다.

~~~powershell
# 로컬 단위 테스트
.\gradlew.bat :app:testDebugUnitTest

# 에뮬레이터 부팅 및 연결 완료 후 기기 테스트
.\gradlew.bat :app:connectedDebugAndroidTest
~~~

### 9.5. 결과 리포트

| 구분 | 경로 | 리포트 생성일 |
|---|---|---|
| 단위 테스트 | `app/build/reports/tests/testDebugUnitTest/index.html` | 2026-11-11 |
| 기기 테스트 | `app/build/reports/androidTests/connected/debug/index.html` | 2026-11-17 |

### 9.6. 실행 환경 문제 및 대응

| 문제 | 점검 및 대응 |
|---|---|
| `JAVA_HOME is set to an invalid directory` | Java 경로 및 Android Studio 내장 JBR 위치 확인 |
| `No connected devices!` | 에뮬레이터 실행 및 ADB 연결 점검 |
| `Failed to install split APK(s)` | 부팅 및 설치 서비스 상태 점검 |
| `Can't find service: package/activity` | 기존 환경 오류 확인 후 Pixel 9에서 테스트 완료 |

## 10. 시연 영상 및 최종 제출

제출 마감은 **2026년 12월 15일(화)**이며, 최종 데모는 시연 영상으로 대체합니다.

| 제출물 | 링크 또는 상태 |
|---|---|
| GitHub | https://github.com/junepark12/mobile-software-term-project_team-6 |
| README | 본 문서 |
| YouTube | 촬영 및 업로드 후 링크 추가 예정 |

영상에는 음식 입력·사진 등록·영양 계산, 목록·상세 조회, 날짜별 요약, 최근 30일 분석, 데이터 출처 화면과 단위·UI 테스트 결과를 포함합니다.

### 과제 제출 기준

| 항목 | 안내문 기준 | 본 프로젝트 |
|---|---|---|
| 최종 제출물 | GitHub 저장소 링크, README.md, YouTube 시연 링크 | 저장소 및 본 문서, 시연 링크는 업로드 후 반영 |
| README 내용 | 앱 소개, 설치 및 실행 방법, 영양 데이터 출처, 역할 분담, 팀원별 Vibe Coding 사용 목록 | 1·4·6·7·8절 |
| 음식 데이터 | 공공 데이터베이스 자료 약 100개 사용, 앱 내 출처 표시 | 100개 음식, 출처/정보 화면 제공; 원본 데이터셋 상세 출처는 확인 필요 |
| 로컬 단위 테스트 | 핵심 로직 12개 이상 | 핵심 로직 16개 |
| Compose UI 테스트 | 2개 이상 | 3개 |
| 개인별 프롬프트 | 실제 입력한 프롬프트로 각자의 구현 과정을 설명 | 8절 개인별 목록 및 PDF |
| 시연 영상 | 구현한 모든 기능과 단위·UI 테스트 실행 결과 포함 | 촬영 및 업로드 후 링크 반영 |

개인별 프롬프트 PDF는 저장소의 `docs` 폴더에 넣어 8절의 상대 경로 링크로 열 수 있도록 구성합니다. 안내문은 개인별 사용 목록을 요구하며, PDF 형식 자체를 필수로 지정하지는 않습니다.

