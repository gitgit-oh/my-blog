package com.blog.mapper;

import com.blog.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select("SELECT id, outline_id, title, sort_order, created_at, updated_at FROM article WHERE outline_id = #{outlineId} ORDER BY sort_order ASC, id DESC")
    List<Article> findByOutlineId(Integer outlineId);

    @Select("SELECT * FROM article WHERE id = #{id}")
    Article findById(Integer id);

    @Insert("INSERT INTO article(outline_id, title, content, sort_order) VALUES(#{outlineId}, #{title}, #{content}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Article article);

    @Update("UPDATE article SET outline_id = #{outlineId}, title = #{title}, content = #{content}, sort_order = #{sortOrder}, updated_at = NOW() WHERE id = #{id}")
    int update(Article article);

    @Delete("DELETE FROM article WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM article WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<Article> searchByKeyword(String keyword);

    @Select("<script>" +
            "SELECT id, outline_id, title, sort_order, created_at, updated_at FROM article WHERE 1=1" +
            "<if test='outlineId != null'> AND outline_id = #{outlineId}</if>" +
            " ORDER BY sort_order ASC, id DESC LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<Article> findByPage(@Param("outlineId") Integer outlineId,
                             @Param("offset") int offset,
                             @Param("size") int size);

    @Select("SELECT COUNT(*) FROM article")
    int countAll();

    @Select("<script>" +
            "SELECT COUNT(*) FROM article WHERE 1=1" +
            "<if test='outlineId != null'> AND outline_id = #{outlineId}</if>" +
            "</script>")
    int countByFilter(@Param("outlineId") Integer outlineId);
}
