package com.ongil.backend.domain.search.repository;

import com.ongil.backend.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends JpaRepository<Brand, Long> {

    @Query(value = """
        SELECT c.category_id, c.name 
        FROM categories c 
        WHERE REPLACE(c.name, ' ', '') LIKE CONCAT('%', :keyword, '%')
        LIMIT :limitCount
        """, nativeQuery = true)
    List<Object[]> searchCategories(@Param("keyword") String keyword, @Param("limitCount") int limitCount);

    @Query(value = """
        SELECT b.brand_id, b.name 
        FROM brands b 
        WHERE REPLACE(b.name, ' ', '') LIKE CONCAT('%', :keyword, '%')
        LIMIT :limitCount
        """, nativeQuery = true)
    List<Object[]> searchBrands(@Param("keyword") String keyword, @Param("limitCount") int limitCount);

    @Query(value = """
        SELECT p.product_id, p.name 
        FROM products p 
        WHERE (REPLACE(p.name, ' ', '') LIKE CONCAT('%', :keyword, '%') 
           OR REPLACE(p.colors, ' ', '') LIKE CONCAT('%', :keyword, '%'))
        LIMIT :limitCount
        """, nativeQuery = true)
    List<Object[]> searchProducts(@Param("keyword") String keyword, @Param("limitCount") int limitCount);
}