package com.blog.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Article implements Serializable {
    private static final long serialVersionUID = -7555761966982120781L;
    private Integer id;
    private Integer outlineId;
    private String title;
    private String content;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
