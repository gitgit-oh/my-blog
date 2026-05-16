package com.blog.mapper;

import com.blog.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category ORDER BY sort_order, id")
    List<Category> findAll();

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(Integer id);

    @Insert("INSERT INTO category(name, sort_order) VALUES(#{name}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("UPDATE category SET name = #{name}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Integer id);
}
