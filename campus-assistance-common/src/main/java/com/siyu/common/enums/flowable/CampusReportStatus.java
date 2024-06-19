package com.siyu.common.enums.flowable;

import lombok.Getter;

@Getter
public enum CampusReportStatus {

    REPORTED("已上报"),

    REJECTED("已驳回"),

    DONE("已办理");

    private final String status;

    CampusReportStatus(String status) {
        this.status = status;
    }
}
