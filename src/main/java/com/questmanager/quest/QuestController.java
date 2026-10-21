package com.questmanager.quest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.questmanager.quest.dto.QuestResponse;
import java.util.List;

// 클래스가 웹 요청을 처리하고 반환값을 응답 본문으로 제공하도록 함
@RestController
public class QuestController{

//    // 특정 api의 GET 요청을 이 메서드에 연결
//    @GetMapping("/api/quests")
//    // 반환한 Java 리스트가 배열로 변환되어 응답
//    public List<String> getQuests() {
//        return List.of(
//                "퀘스트1",
//                "퀘스트2",
//                "퀘스트3"
//        );
//    }

    // Spring이 자동으로 QusetService 객체를 생성해서 주입
    // 직접 new 할당하지 않고 Spring이 관리
    @Autowired
    QuestService questService;

    // Get /api/quests 요청이 이 메서드로 들어옴
    // :13과는 다름
    @GetMapping("/api/quests")
    public List<QuestResponse> getAll() {
        return questService.getAllQuests()
                .stream() // List를 stream으로 변환
                .map(QuestResponse::new) // 각 QuestDefinition를 QuestResponse로 변환, new QuestResponse(questDef) 자동 반복 호출
                .toList(); // stream을 다시 List로 변환
    }

    // ResponseEntity는 new 할당 안하고 정적 메서드 사용,
    // 반환 데이터가 복잡해지면 다른 메서드로 체이닝해서 반환한다
    @GetMapping("/api/quests/{questId}")
    public ResponseEntity<?> getQuestById(@PathVariable int questId) {
        QuestDefinition def = questService.getQuestById(questId);

        if(def == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(def);
    }
}

// 메서드 참조(::) 문법
// 람다 표현식이 하나의 메서드만 호출할 때 매개변수 선언을 생략하고 간결하게 작성하는 방법
// (x) -> Class.Method(x)
// Class::Method