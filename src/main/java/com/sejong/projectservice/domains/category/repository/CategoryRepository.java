package com.sejong.projectservice.domains.category.repository;

import com.sejong.projectservice.domains.category.domain.CategoryEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByName(String name);

    List<CategoryEntity> findAllByNameIn(List<String> names);

    @Query("select count(*) from CategoryEntity ce")
    Long findAllCategoryCount();
}
