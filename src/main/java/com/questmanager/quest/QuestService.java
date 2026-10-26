// 비즈니스 로직 담당 중간 계층
// Controller - DB 사이 연결
package com.questmanager.quest;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.questmanager.quest.QuestRepository;

//@Service로 Spring이 자동 관리
@Service
public class QuestService {
    @Autowired
    private QuestRepository questRepository;


    public List<QuestDefinition> getAllQuests() {
      return questRepository.findAll();
    }

    // Optional은 값이 있을수도 있고 없을수도 있는 객체를 감싸는 래퍼 클래스
    public Optional<QuestDefinition> getQuestById(Integer questId) {return questRepository.findById(questId); }

    // Data Store
    // 임시로 메모리에 저장
//    private Map<Integer, QuestDefinition> quests = new HashMap<>();
//    public QuestService() {
//        initQuests();
//    }
//    private void initQuests() {
//        quests.put(1, new QuestDefinition(
//                1, "Defeat Goblins", "Get 10 Goblins", 1, 100, 50, 10
//        ));
//    }
//    // 조회 메서드
//    public List<QuestDefinition> getAllQuests() {
//        return new ArrayList<>(quests.values());
//    }
//    public QuestDefinition getQuestById(int questId) {
//        return quests.get(questId);
//    }

    @PostConstruct
    public void loadInitialData(){
        questRepository.saveAll(List.of(
                new QuestDefinition(1, "Defeat Goblins", "Get 10 Goblins", 1, 100, 50, 10),
                new QuestDefinition(2, "Collect Herbs", "Gather 5 herbs from the forest", 1, 50, 30, 5),
                new QuestDefinition(3, "Slay the Dragon", "Defeat the mighty dragon", 10, 500, 300, 1),
                new QuestDefinition(4, "Find the Lost Artifact", "Locate the ancient artifact", 5, 200, 150, 3)
        ));
    }
}