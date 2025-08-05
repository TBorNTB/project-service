package com.sejong.projectservice.infrastructure.project.repository;

import com.sejong.projectservice.infrastructure.project.entity.ProjectDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProjectElasticDocumentRepository extends ElasticsearchRepository<ProjectDocument,String> {
    void deleteById(Long projectId);
}
