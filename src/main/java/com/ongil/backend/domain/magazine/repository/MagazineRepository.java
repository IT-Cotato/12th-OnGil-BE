package com.ongil.backend.domain.magazine.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ongil.backend.domain.magazine.entity.Magazine;
import com.ongil.backend.domain.magazine.enums.MagazineCategory;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long> {

	@Query(value = "SELECT * FROM magazines ORDER BY RAND()", nativeQuery = true)
	List<Magazine> findRandomRecommended(Pageable pageable);

	List<Magazine> findByCategoryOrderByViewCountDescPublishedAtDesc(MagazineCategory category, Pageable pageable);

	@Query("select m.url from Magazine m where m.url in :urls")
	Set<String> findAllUrlsByUrlIn(@Param("urls") List<String> urls);
}