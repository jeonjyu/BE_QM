# Quest Manager API - 4주 학습 계획

Spring Boot 학습 프로젝트: RPG 게임의 퀨스트 관리 REST API

**학습 목표**: C#/Unity 경험을 활용하여 Spring Boot 백엔드 개발의 핵심 개념 습득

---

## 📋 1주차: REST API & 기본 쿼리 (메모리 기반)

**목표**: HTTP 요청/응답 처리, JSON 변환, HTTP 상태 코드 이해

### 구현 내용
- ✅ **QuestStatus** enum (AVAILABLE, IN_PROGRESS, COMPLETED, ABANDONED)
- ✅ **QuestDefinition** 클래스 (퀘스트 메타데이터)
  - 필드: id, name, description, requiredLevel, goldReward, experienceReward, progressMax
  - 생성자 및 모든 필드의 getter
- ✅ **QuestResponse** DTO (JSON 응답용)
  - QuestDefinition을 받아서 필드 매핑
  - 모든 필드의 getter (Spring이 JSON 변환 시 사용)
- ✅ **QuestService** (비즈니스 로직)
  - HashMap으로 3-4개 퀨스트 메모리 저장
  - `getAllQuests()`, `getQuestById(int)` 메서드
- ✅ **QuestController** (HTTP 처리)
  - `GET /api/quests` → 모든 퀨스트 조회 (200)
  - `GET /api/quests/{questId}` → 특정 퀨스트 조회 (200 또는 404)

### 학습 포인트
- **@RestController, @GetMapping, @PathVariable**: HTTP 요청 매핑
- **DTO 패턴**: 내부 모델(Definition)과 응답(Response) 분리
- **메서드 참조**: `.map(QuestResponse::new)` 문법
- **HTTP 상태 코드**: 200 (성공), 404 (없음)
- **Spring의 자동 JSON 변환**: getter를 통한 직렬화

### 핵심 개념 (Unity와 연결)
| Unity | Spring Boot |
|-------|------------|
| 게임 매니저 | Service (비즈니스 로직) |
| HTTP 없음 | REST API (클라이언트-서버 통신) |
| 메모리 관리 | DB + Repository (영속성) |

---

## 📋 2주차: JPA & 데이터베이스 연동

**목표**: 메모리 저장소 → DB(H2) 변경, JPA 이해, Repository 패턴 학습

### 구현 내용

#### 1. QuestDefinition을 JPA Entity로 변환
```java
@Entity
@Table(name = "quest_definitions")
public class QuestDefinition {
    @Id
    private int id;
    private String name;
    private String description;
    private int requiredLevel;
    private int goldReward;
    private int experienceReward;
    private int progressMax;
    
    // JPA 필수: 기본 생성자
    public QuestDefinition() {}
    
    // 기존 생성자 유지
    public QuestDefinition(int id, String name, ...) { ... }
}
```

#### 2. QuestRepository 생성
```java
@Repository
public interface QuestRepository extends JpaRepository<QuestDefinition, Integer> {
    // findAll(), findById(), save() 등 자동 제공
}
```

#### 3. QuestService 수정
```java
@Service
public class QuestService {
    @Autowired
    private QuestRepository questRepository;
    
    public List<QuestDefinition> getAllQuests() {
        return questRepository.findAll();
    }
    
    public Optional<QuestDefinition> getQuestById(Integer questId) {
        return questRepository.findById(questId);
    }
}
```

#### 4. 초기 데이터 로드 (선택사항)
- `data.sql` 파일로 INSERT
- 또는 `@PostConstruct`로 프로그래밍

#### 5. application.properties 설정
```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.h2.console.enabled=true
```

### 학습 포인트
- **@Entity, @Id, @Table**: JPA 매핑
- **JpaRepository**: CRUD 자동 제공
- **의존성 주입(@Autowired)**: Spring이 객체 관리
- **Optional**: null-safe 코드 작성
- **메모리 vs DB**: 런타임 종료 후에도 데이터 유지

### 추가 학습 (PlayerQuest 분리)
게임에서 "퀨스트 정의"와 "플레이어 진행도"를 분리하듯이:
- **QuestDefinition**: 마스터 데이터 (변하지 않음, ScriptableObject와 유사)
- **PlayerQuest**: 플레이어별 진행 상태 (변함)
  ```java
  @Entity
  public class PlayerQuest {
      @Id
      private Long id;
      private Integer playerId;
      private Integer questId;
      private QuestStatus status;  // AVAILABLE, IN_PROGRESS, COMPLETED
      private Integer progress;     // 0 ~ progressMax
      private LocalDateTime acceptedAt;
  }
  ```

---

## 📋 3주차: 도메인 규칙 & 트랜잭션

**목표**: 상태 전이 구현, 서버 측 검증, 원자성(atomicity) 이해

### 구현 내용

#### 1. 퀨스트 수락 API
```java
@PostMapping("/api/quests/{questId}/accept")
public ResponseEntity<?> acceptQuest(@PathVariable Integer questId, 
                                     @RequestBody AcceptQuestRequest request) {
    // 플레이어의 현재 레벨 확인
    // 이미 수락했는지 확인 (중복 수락 방지)
    // PlayerQuest 생성
    // 상태: AVAILABLE → IN_PROGRESS
}
```

#### 2. 진행도 업데이트 API
```java
@PutMapping("/api/quests/{questId}/progress")
public ResponseEntity<?> updateProgress(@PathVariable Integer questId,
                                       @RequestBody ProgressUpdateRequest request) {
    // PlayerQuest 조회
    // 진행도 업데이트 (0 ~ progressMax)
    // 진행도 >= progressMax → 완료 가능 상태로 변경
}
```

#### 3. 퀨스트 완료 API (트랜잭션)
```java
@PostMapping("/api/quests/{questId}/complete")
@Transactional  // ← 중요: 모든 작업이 한 번에 처리되거나 모두 롤백
public ResponseEntity<?> completeQuest(@PathVariable Integer questId,
                                      @RequestBody CompleteQuestRequest request) {
    // 1. 상태 검증 (완료 가능한 상태인가?)
    // 2. 보상 지급 (금,경험치 추가)
    // 3. PlayerQuest 상태 변경 (COMPLETED)
    // 4. RewardHistory 기록 (중복 지급 방지)
    // 모두 성공하면 commit, 하나라도 실패하면 rollback
}
```

#### 4. 도메인 규칙 검증
```java
@Service
public class QuestService {
    @Transactional
    public void completeQuest(Integer playerId, Integer questId) {
        PlayerQuest pq = playerQuestRepo.findByPlayerIdAndQuestId(...)
            .orElseThrow(() -> new QuestNotAcceptedException());
        
        if (!pq.canComplete()) {  // 서버 측 검증
            throw new InvalidQuestStateException();
        }
        
        if (rewardHistoryRepo.existsByPlayerAndQuest(playerId, questId)) {
            throw new AlreadyRewardedException();  // 중복 지급 방지
        }
        
        pq.setStatus(QuestStatus.COMPLETED);
        // 보상 지급...
    }
}
```

### 학습 포인트
- **@Transactional**: 여러 작업을 원자적으로 처리
- **도메인 규칙**: 서버에서 반드시 검증해야 할 규칙 정의
- **상태 머신**: AVAILABLE → IN_PROGRESS → COMPLETED 전이만 허용
- **중복 처리**: 같은 요청이 여러 번 들어와도 한 번만 처리
- **롤백**: 실패 시 이전 상태로 복구

### 추가 개념 (DDD 기초)
```java
@Entity
public class PlayerQuest {
    // ...
    
    // 도메인 메서드: 비즈니스 규칙을 클래스 안에 캡슐화
    public boolean canComplete() {
        return status == QuestStatus.IN_PROGRESS 
            && progress >= questDefinition.getProgressMax();
    }
    
    public void complete() {
        if (!canComplete()) {
            throw new InvalidQuestStateException();
        }
        this.status = QuestStatus.COMPLETED;
    }
}
```

---

## 📋 4주차: 테스트 & 완성

**목표**: JUnit으로 비즈니스 로직 검증, Spring Test로 API 통합 테스트, 엣지 케이스 처리

### 구현 내용

#### 1. 단위 테스트 (JUnit)
```java
@Test
public void testCanCompleteQuest() {
    PlayerQuest pq = new PlayerQuest();
    pq.setStatus(QuestStatus.IN_PROGRESS);
    pq.setProgress(10);
    
    assertTrue(pq.canComplete());  // 완료 가능
    
    pq.setProgress(5);
    assertFalse(pq.canComplete());  // 완료 불가
}

@Test
public void testDuplicateAccept() {
    // 같은 퀨스트를 두 번 수락 시도 → 예외 발생
    questService.acceptQuest(playerId, questId);
    assertThrows(DuplicateQuestAcceptException.class, 
        () -> questService.acceptQuest(playerId, questId));
}
```

#### 2. 통합 테스트 (Spring Test)
```java
@SpringBootTest
@AutoConfigureMockMvc
public class QuestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetAllQuests() throws Exception {
        mockMvc.perform(get("/api/quests"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Defeat Goblins"));
    }
    
    @Test
    public void testGetNonexistentQuest() throws Exception {
        mockMvc.perform(get("/api/quests/999"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    public void testCompleteQuestFlow() throws Exception {
        // 1. 수락
        mockMvc.perform(post("/api/quests/1/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"playerId\": 1}"))
            .andExpect(status().isOk());
        
        // 2. 진행도 업데이트
        mockMvc.perform(put("/api/quests/1/progress")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"progress\": 10}"))
            .andExpect(status().isOk());
        
        // 3. 완료
        mockMvc.perform(post("/api/quests/1/complete")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"playerId\": 1}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.goldReward").value(100));
    }
}
```

#### 3. 엣지 케이스 테스트
```java
@Test
public void testCompleteWithoutAccept() {
    // 수락하지 않고 완료 시도
    assertThrows(QuestNotAcceptedException.class,
        () -> questService.completeQuest(playerId, questId));
}

@Test
public void testDoubleReward() {
    // 같은 퀨스트 두 번 완료 시도 (보상 중복 지급 방지)
    questService.completeQuest(playerId, questId);
    assertThrows(AlreadyRewardedException.class,
        () -> questService.completeQuest(playerId, questId));
}

@Test
public void testConcurrentAccept() {
    // 동시에 같은 퀨스트 수락 시도
    // @Transactional과 unique constraint로 보호
}
```

#### 4. README 완성
- API 엔드포인트 정리
- 테스트 실행 방법
- 설계 결정 사항 (왜 이렇게 했는가)

### 학습 포인트
- **JUnit**: 단위 테스트 (비즈니스 로직)
- **Spring Test & MockMvc**: HTTP 계층 테스트
- **테스트 피라미드**: 단위 테스트 많음 → 통합 테스트 중간 → E2E 테스트 적음
- **엣지 케이스**: null, 중복, 동시성, 권한 등
- **테스트 주도 개발(TDD)**: 테스트 → 구현 순서

---

## 🎓 학습 성과

### 1주차 이후
- HTTP 요청/응답 이해
- JSON 직렬화/역직렬화
- DTO 패턴 적용

### 2주차 이후
- JPA로 DB 매핑
- Repository 패턴 이해
- 데이터 영속성 확보

### 3주차 이후
- 트랜잭션과 원자성 이해
- 도메인 규칙을 서버에서 검증
- 상태 머신 설계

### 4주차 이후
- 비즈니스 로직 검증 능력
- API 엣지 케이스 처리
- 실전 개발 경험

---

## 📚 Unity와의 개념 매핑

| Unity | Spring Boot | 용도 |
|-------|------------|------|
| MonoBehaviour | @Service, @Controller | 객체 생명주기 관리 |
| ScriptableObject | @Entity | 데이터 정의 |
| 게임 상태(enum) | QuestStatus enum | 도메인 상태 |
| 게임 로직(메서드) | 도메인 메서드 | 비즈니스 규칙 |
| 유닛 테스트 | JUnit | 로직 검증 |
| 네트워크 통신 | REST API | 클라이언트-서버 통신 |
| 데이터 저장 | DB + JPA | 영속성 |

---

## 🚀 다음 단계 (선택사항)

**5주차 이후:**
- 플레이어 인증 (JWT)
- API 권한 검증
- 실시간 멀티플레이 (WebSocket)
- 성능 최적화 (캐싱, 쿼리 최적화)
- 배포 (Docker, AWS)

---

**학습 기간**: 4주 × 5~10시간 = 20~40시간
**목표**: Spring Boot 백엔드의 핵심 개념 이해 및 실전 경험
