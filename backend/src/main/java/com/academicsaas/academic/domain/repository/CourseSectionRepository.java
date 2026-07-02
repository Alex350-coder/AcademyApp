package com.academicsaas.academic.domain.repository;

import com.academicsaas.academic.domain.model.CourseSection;
import java.util.Optional;
import java.util.UUID;

public interface CourseSectionRepository {

    CourseSection save(CourseSection section);

    Optional<CourseSection> findById(UUID id);
}
