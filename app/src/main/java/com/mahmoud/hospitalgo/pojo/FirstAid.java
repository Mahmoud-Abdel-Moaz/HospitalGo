package com.mahmoud.hospitalgo.pojo;

import java.util.ArrayList;
import java.util.List;

public class FirstAid {
    String name;
    List<Step> steps;
    public FirstAid() {
        this.steps = new ArrayList<>();
    }

    public FirstAid(String name, List<Step> steps) {
        this.name = name;
        this.steps = new ArrayList<>();
        this.steps.addAll(steps);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
