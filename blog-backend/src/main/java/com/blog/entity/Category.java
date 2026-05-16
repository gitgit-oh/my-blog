package com.blog.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Category implements Serializable {
    private Integer id;
    private String name;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
