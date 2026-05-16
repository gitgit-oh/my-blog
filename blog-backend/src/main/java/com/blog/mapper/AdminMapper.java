package com.blog.mapper;

import com.blog.entity.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {

    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(String username);

    @Insert("INSERT INTO admin(username, password_hash) VALUES(#{username}, #{passwordHash})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    @Update("UPDATE admin SET password_hash = #{passwordHash} WHERE username = #{username}")
    int updatePassword(@Param("username") String username, @Param("passwordHash") String passwordHash);
}
