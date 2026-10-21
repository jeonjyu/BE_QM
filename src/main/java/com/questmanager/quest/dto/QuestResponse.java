package com.questmanager.quest.dto;

import com.questmanager.quest.QuestDefinition;

public class QuestResponse {

    private int id;
    private String name;
    private String description;
    private int requireLevel;
    private int goldReward;
    private int experienceReward;
    private int progressMax;

    public QuestResponse(QuestDefinition def){
        this.id = def.getId();
        this.name = def.getName();
        this.description = def.getDescription();
        this.requireLevel = def.getRequiredLevel();
        this.goldReward = def.getGoldReward();
        this.experienceReward = def.getExperienceReward();
        this.progressMax = def.getProgressMax();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRequireLevel() {
        return requireLevel;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getExperienceReward() {
        return experienceReward;
    }

    public int getProgressMax() {
        return progressMax;
    }
}