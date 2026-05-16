package com.blog.repository;

import com.blog.entity.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ArticleSearchRepository extends ElasticsearchRepository<ArticleDocument, Integer> {

    List<ArticleDocument> findByTitleContaining(String title);
}
