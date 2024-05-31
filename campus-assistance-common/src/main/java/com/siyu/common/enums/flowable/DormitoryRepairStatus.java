package com.siyu.common.enums.flowable;

import lombok.Getter;

@Getter
public enum DormitoryRepairStatus {

    REPORTED("已上报待维修"),

    REPAIRED("已维修待评分"),

    RATED("已评分");

    private final String status;

    DormitoryRepairStatus(String status) {
        this.status = status;
    }
}
