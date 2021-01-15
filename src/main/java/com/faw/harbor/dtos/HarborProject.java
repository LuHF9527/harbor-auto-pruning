package com.faw.harbor.dtos;

/**
 * @Author Ron
 * @create 2021-1-8 13:31
 */
public class HarborProject {
    private String name;
    private Integer project_id;

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
