package com.siyu.common.enums;

import lombok.Getter;

@Getter
public enum DepartmentLevel {

    UNIVERSITY(1, "校级"),

    COLLEGE(2, "院级"),

    DEPARTMENT(3, "系级 "),

    GRADE(4, "年级"),

    CLASS(5, "班级")
    ;

    private final Integer level;

    private final String name;

    DepartmentLevel(int level, String name) {
        this.level = level;
        this.name = name;
    }
}
