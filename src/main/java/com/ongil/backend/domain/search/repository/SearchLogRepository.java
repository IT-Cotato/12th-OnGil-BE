package com.ongil.backend.domain.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.ongil.backend.domain.search.document.SearchLogDocument;

@Repository
public interface SearchLogRepository extends ElasticsearchRepository<SearchLogDocument, String> {
}