package com.blog.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Outline implements Serializable {
    private Integer id;
    private Integer categoryId;
    private String title;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
