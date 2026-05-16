package com.blog.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String username;
    private String passwordHash;
    private LocalDateTime createdAt;
}
