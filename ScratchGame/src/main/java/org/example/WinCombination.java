package org.example;

import java.util.List;

class WinCombination {
    private double rewardMultiplier;
    private String when;
    private Integer count;
    private String group;
    private List<List<String>> coveredAreas;

    // Getters and setters
    public double getRewardMultiplier() { return rewardMultiplier; }
    public void setRewardMultiplier(double rewardMultiplier) { this.rewardMultiplier = rewardMultiplier; }
    public String getWhen() { return when; }
    public void setWhen(String when) { this.when = when; }
    public Integer getCount() { return count != null ? count : 0; }
    public void setCount(Integer count) { this.count = count; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
    public List<List<String>> getCoveredAreas() { return coveredAreas; }
    public void setCoveredAreas(List<List<String>> coveredAreas) { this.coveredAreas = coveredAreas; }
}