package com.task.tracker.dto;

import lombok.Data;

@Data
public class StatusDTO {
    private Long id;
    private String title;
    private Boolean isGlobal;
    private Long userId;
}