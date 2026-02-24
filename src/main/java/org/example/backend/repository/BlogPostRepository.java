package org.example.backend.repository;

import org.example.backend.model.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Page<BlogPost> findByIsPublishedTrue(Pageable pageable);

    BlogPost findBySlug(String slug);
}
