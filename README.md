# 🍱 학생 식당 개인 사용 관리 앱 (Term Project)

> **2026년 모바일 소프트웨어 학기말 프로젝트**  
> Android Studio, Kotlin, Jetpack Compose, Room Database를 활용한 학생 식당 식단 & 영양 관리 모바일 애플리케이션

---

## 1. 📌 프로젝트 소개

본 프로젝트는 대학생 및 학생 식당 이용자가 매일 섭취하는 식단과 영양 성분을 효율적으로 기록하고 분석할 수 있는 **'학생 식당 개인 사용 관리 앱'**입니다.  
식품의약품안전처의 검증된 공공데이터(약 100여 개 식품 영양 DB)를 기반으로 식사 섭취량(g)에 따른 탄수화물, 단백질, 지방, 칼로리를 실시간으로 자동 계산하며, 날짜별 히스토리 및 최근 1개월 식단 칼로리 추이와 끼니별(조식·중식·석식·간식/음료) 영양소 분포를 직관적인 차트와 카드로 시각화합니다.

---

## 2. ✨ 주요 기능

| 기능 카테고리 | 상세 설명 |
| :--- | :--- |
| **1. 음식 입력 (Meal Input)** | • 식사 날짜(`DatePickerDialog`) 및 시간(`TimePickerDialog`) 선택<br>• 조식/중식/석식/간식·음료 4대 끼니 구분 지원<br>• Android Photo Picker & 갤러리를 통한 식사 사진 등록 (지속적 URI 권한 유지)<br>• 약 100개 공공 DB 음식 검색 및 섭취량(g) 조절<br>• **탄수화물·단백질·지방·칼로리 실시간 미리보기** 및 Room DB 단일 원자적 트랜잭션(`@Transaction`) 저장 |
| **2. 식사 조회 (Meal History)** | • 최신순 정렬 식사 카드 리스트 및 **날짜별 그룹화 헤더** (일일 총 칼로리, 끼니 수 표시)<br>• 항목 선택 시 상세 화면 이동 (`mealId` 전달)<br>• **식사 상세 조회**: 섭취 사진, 구성 음식/반찬 목록, 섭취량(g), 탄·단·지 영양소 breakdown 및 삭제 기능 |
| **3. 식사 분석 (Meal Analysis)** | • **최근 1개월(30일 실제 날짜 범위) 분석**: 총 섭취 칼로리 및 30일 하루 평균 칼로리 집계<br>• **30일 칼로리 추이 막대 차트 (`MonthlyCalorieChart`)**: 기록 없는 날짜 `0 kcal` 표시 및 터치 피드백<br>• **1개월 총 영양소 분석**: 총 탄수화물, 단백질, 지방량 및 비율 프로그레스 바<br>• **끼니별 비율 도넛 차트 (`MealTypeDonutChart`)**: 조식·중식·석식·간식/음료별 칼로리 비율 및 상세 통계 카드 |
| **4. 공공 DB 및 출처 표기** | • 식품의약품안전처 표준 식품 100개 영양 정보 내장 (`assets/foods.json`)<br>• 출처 정보 화면(`InfoScreen`)을 통한 공공데이터 라이선스 및 데이터베이스 출처 명시 |

---

## 3. 🛠️ 개발 환경 및 기술 스택

- **JDK / Kotlin**: OpenJDK 11 / Kotlin `2.2.10`
- **Android SDK**: Compile / Target SDK `37`, Min SDK `28`
- **Build System**: Android Gradle Plugin (AGP) `9.3.3`, Gradle Version Catalog (`libs.versions.toml`)
- **UI Framework**: Jetpack Compose (Material3), Compose Navigation, Compose Material Icons Extended
- **Architecture**: MVVM Architecture + Repository Pattern + Clean Layering
- **Database**: Room Database (`2.8.5`), KSP (`2.3.13`)
- **Image Loading**: Coil Compose (`2.7.0`)
- **Testing**: JUnit4, AndroidX Compose UI Testing (`ui-test-junit4`)

---

## 4. 🚀 설치 및 실행 방법

### 1) 프로젝트 클론 및 빌드
```bash
# 저장소 복사
git clone https://github.com/your-repo/termproject.git
cd termproject

# Debug APK 빌드
./gradlew app:assembleDebug
```

### 2) 앱 실행
1. Android Studio에서 프로젝트를 오픈합니다.
2. Android 9.0 (API 28) 이상의 에뮬레이터 또는 USB 디버깅이 활성화된 실기기를 연결합니다.
3. 상단 실행 버튼(`Shift + F10`)을 눌러 `com.example.termproject` 앱을 실행합니다.

---

## 5. 🗄️ 데이터베이스 구조 (Database Schema)

Room DB는 **`cafeteria_meal_db`** 데이터베이스명을 사용하며, 식사와 세부 음식 간의 1:N 릴레이션을 캡슐화하고 CASCADE 연쇄 삭제 및 `@Transaction` 원자적 저장을 보장합니다.

```
┌─────────────────────────────────┐
│           foods                 │
├─────────────────────────────────┤
│ foodId (PK, AutoGenerate)       │
│ foodCode (String?, Nullable)    │
│ foodName (String)               │
│ category (String)               │
│ carbohydratePer100g (Double)    │
│ proteinPer100g (Double)         │
│ fatPer100g (Double)             │
│ caloriePer100g (Double)         │
│ dataSource (String)             │
│ isVerified (Boolean)            │
└─────────────────────────────────┘
                 ▲ (1:N SET_NULL)
┌─────────────────────────────────┐       ┌─────────────────────────────────┐
│           meals                 │       │           meal_items            │
├─────────────────────────────────┤       ├─────────────────────────────────┤
│ mealId (PK, AutoGenerate)       │◄──────│ mealItemId (PK, AutoGenerate)   │
│ mealDate (String, YYYY-MM-DD)   │ (1:N) │ mealId (FK, CASCADE)            │
│ mealTime (String, HH:mm)        │       │ foodId (FK, SET_NULL)           │
│ mealType (String)               │       │ foodName (String)               │
│ imageUri (String?, Nullable)    │       │ quantityGram (Double)           │
│ createdAt (Long)                │       │ carbohydratePer100g (Double)    │
└─────────────────────────────────┘       │ proteinPer100g (Double)         │
                                          │ fatPer100g (Double)             │
                                          │ caloriePer100g (Double)         │
                                          └─────────────────────────────────┘
```

---

## 6. 🏛️ 공공 영양 데이터 출처

- **데이터 제공 기관**: 대한민국 식품의약품안전처 (공공데이터포털 `www.data.go.kr`)
- **데이터베이스 명칭**: **식품의약품안전처_식품 영양성분 DB** (오픈 API 서비스 ID: `I2790` / `C003`)
- **보관 데이터 및 기준**: `assets/foods.json` 내 10개 카테고리 100개 표준 식사/학식 메뉴 (100g 당 영양성분 수치 정제 완료)
- **앱 내 출처 표시**: 하단 네비게이션 탭의 `출처/정보` 화면(`InfoScreen`) 및 검색 결과 카드에 식약처 출처 상시 명시

---

## 7. 👥 팀원 역할 분담

| 팀원 이름 | 담당 역할 |
| :--- | :--- |
| **개발자 A** | • 안드로이드 프로젝트 아키텍처 설계 (MVVM, Repository 패턴 구성)<br>• Room Database 엔티티, DAO, 트랜잭션(`@Transaction`) 및 `assets/foods.json` 100개 공공 DB 구축<br>• `NutrientCalculator`, `DailySummaryCalculator`, `MealAnalysisCalculator` 핵심 도메인 로직 구현 및 17개 로컬 단위 테스트 작성 |
| **개발자 B** | • Jetpack Compose 디자인 시스템 구현 (`Color.kt`, `Theme.kt`, `Type.kt`, 공통 카드/버튼/입력창)<br>• Navigation Compose 및 하단 네비게이션 바 구축<br>• `HomeScreen`, `MealInputScreen`, `MealListScreen`, `MealDetailScreen`, `AnalysisScreen`, `FoodSearchScreen`, `InfoScreen` 전체 UI 스크린 및 Compose UI 테스트 작성 |

---

## 8. 🤖 팀원별 AI Vibe Coding 사용 목록

- **사용 AI 스택**: Gemini / Android Studio Bot
- **주요 AI Vibe Coding 활용 내역**:
  1. **데이터베이스 & KSP 스키마 프롬프팅**: Room Entity 1:N 관계 정의 시 `CASCADE` 삭제 조건 및 KSP `2.3.13` 빌드 호환성 오류 해결 가이드 생성.
  2. **비량 계산 알고리즘 및 BDD 단위 테스트 생성**: 100g 기준 영양성분 비례 공식 `(base * quantity) / 100` 및 예외 검증을 위한 JUnit4 테스트 케이스 17종 자동 보완.
  3. **Compose Custom Canvas 차트 구현**: 별도 외부 차트 라이브러리 없이 Compose Native `Canvas`를 이용한 30일 칼로리 추이 막대 그래프 및 끼니별 칼로리 비율 도넛 차트 렌더링 코드 생성.
  4. **Android Photo Picker & Persistable URI Permission 처리**: Android 13+ Photo Picker와 구버전 갤러리 런처 및 `takePersistableUriPermission` 권한 유지 보일러플레이트 작성.

---

## 9. 🧪 테스트 실행 방법 및 결과

본 프로젝트는 에뮬레이터 없이 JVM에서 실행되는 **로컬 단위 테스트 17개**와 **Compose UI 테스트 3개**를 포함합니다.

### 1) 단위 테스트 (Local Unit Tests - 17/17 Passed)
- **실행 명령**: `./gradlew app:testDebugUnitTest`
- **테스트 케이스 결과 요약**:
  - `NutrientCalculatorTest`: 100g / 150g / 200g 비례 계산, 다중 음식 합산, 0g 및 음수 입력 예외 검증 (9/9 Passed)
  - `DailySummaryCalculatorTest`: 날짜별 식사 그룹화, 총칼로리 및 끼니 수 집계, 최신순 정렬, 빈 데이터 처리 (3/3 Passed)
  - `MealAnalysisCalculatorTest`: 최근 1개월(30일) 날짜 범위 필터링, 기록 없는 날짜 0 kcal 처리, 조식·중식·석식·간식별 통계 (3/3 Passed)
  - `FoodSearchLogicTest`: 음식 키워드 검색 및 카테고리 필터링 (2/2 Passed)

### 2) Compose UI 테스트 (AndroidX Compose UI Testing - 3/3 Passed)
- **실행 명령**: `./gradlew app:connectedAndroidTest` (에뮬레이터 또는 기기 필요)
- **테스트 케이스 결과 요약**:
  - `MealInputUiTest`: 식사 등록 화면 타이틀, 날짜/시간 피커, 식사 구분 칩, 음식 추가 버튼 및 저장 버튼 렌더링 검증
  - `MealListUiTest`: 식사 기록 목록 화면 타이틀, 빈 상태 안내 문구(`등록된 식사 기록이 없습니다.`) 및 데이터 주입 시 카드 렌더링 검증

---

## 📹 제출물 안내

1. **GitHub 저장소 링크**: [https://github.com/your-username/termproject](https://github.com/your-username/termproject)
2. **README.md 문서**: 본 문서 (`README.md`)
3. **YouTube 시연 영상 링크**: [https://youtu.be/your-demo-video-id](https://youtu.be/your-demo-video-id)
