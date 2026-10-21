// 비즈니스 로직 담당 중간 계층
// Controller - DB 사이 연결
package com.questmanager.quest;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service로 Spring이 자동 관리
@Service
public class QuestService {
    // Data Store
    // 임시로 메모리에 저장
    private Map<Integer, QuestDefinition> quests = new HashMap<>();

    public QuestService() {
        initQuests();
    }

    private void initQuests() {
        quests.put(1, new QuestDefinition(
                1, "Defeat Goblins", "Get 10 Goblins", 1, 100, 50, 10
        ));
    }

    // 조회 메서드
    public List<QuestDefinition> getAllQuests() {
        return new ArrayList<>(quests.values());
    }

    public QuestDefinition getQuestById(int questId) {
        return quests.get(questId);
    }
}