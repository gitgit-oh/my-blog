package com.blog.mapper;

import com.blog.entity.Outline;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OutlineMapper {

    @Select("SELECT * FROM outline ORDER BY sort_order, id")
    List<Outline> findAll();

    @Select("SELECT * FROM outline WHERE category_id = #{categoryId} ORDER BY sort_order, id")
    List<Outline> findByCategoryId(Integer categoryId);

    @Select("SELECT * FROM outline WHERE id = #{id}")
    Outline findById(Integer id);

    @Insert("INSERT INTO outline(category_id, title, sort_order) VALUES(#{categoryId}, #{title}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Outline outline);

    @Update("UPDATE outline SET category_id = #{categoryId}, title = #{title}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(Outline outline);

    @Delete("DELETE FROM outline WHERE id = #{id}")
    int deleteById(Integer id);
}
