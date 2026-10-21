package com.questmanager.quest;

public class QuestDefinition {

    public QuestDefinition(int id, String name, String description, int requiredLevel, int goldReward, int experienceReward, int progressMax) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredLevel = requiredLevel;
        this.goldReward = goldReward;
        this.experienceReward = experienceReward;
        this.progressMax = progressMax;
    }

    private final int id;
    private final String name;
    private final String description;
    private final int requiredLevel;
    private final int goldReward;
    private final int experienceReward;
    private final int progressMax;


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