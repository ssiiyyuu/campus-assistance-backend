package com.siyu.common.enums.flowable;

import lombok.Getter;

@Getter
public enum HolidayStatus {

    APPLYING("申请中"),

    LEAVING("休假中"),

    REJECTED("已驳回"),

    LEAVED("已休假");

    private final String status;

    HolidayStatus(String status) {
        this.status = status;
    }
}
