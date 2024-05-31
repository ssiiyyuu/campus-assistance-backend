package com.siyu.server.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class LocationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    //经度
    private Long longitude;

    @NotNull
    //纬度
    private Long latitude;

}
