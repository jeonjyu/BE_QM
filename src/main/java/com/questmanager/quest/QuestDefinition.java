package com.questmanager.quest;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table
public class QuestDefinition {

    public QuestDefinition() {}
    public QuestDefinition(int id, String name, String description, int requiredLevel, int goldReward, int experienceReward, int progressMax) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredLevel = requiredLevel;
        this.goldReward = goldReward;
        this.experienceReward = experienceReward;
        this.progressMax = progressMax;
    }

    @Id
    private int id;
    private String name;
    private String description;
    private int requiredLevel;
    private int goldReward;
    private int experienceReward;
    private int progressMax;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredLevel() {
        return requiredLevel;
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