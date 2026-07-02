package com.academicsaas.academic.application.usecase;

import com.academicsaas.academic.domain.exception.DuplicateEnrollmentException;
import com.academicsaas.academic.domain.model.Enrollment;
import com.academicsaas.academic.domain.repository.CourseSectionRepository;
import com.academicsaas.academic.domain.repository.EnrollmentRepository;
import com.academicsaas.shared.exception.NotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollStudentUseCase {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseSectionRepository courseSectionRepository;

    public EnrollStudentUseCase(
        EnrollmentRepository enrollmentRepository,
        CourseSectionRepository courseSectionRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseSectionRepository = courseSectionRepository;
    }

    public record Request(UUID studentId, UUID sectionId) {}

    public record Response(UUID enrollmentId) {}

    public Response execute(Request request) {
        var section = courseSectionRepository.findById(request.sectionId())
            .orElseThrow(() -> new NotFoundException("CourseSection", request.sectionId()));

        if (enrollmentRepository.existsByStudentIdAndSectionId(request.studentId(), request.sectionId())) {
            throw new DuplicateEnrollmentException(
                request.studentId().toString(),
                request.sectionId().toString()
            );
        }

        section.incrementEnrolledCount();
        courseSectionRepository.save(section);

        var enrollment = Enrollment.create(
            UUID.randomUUID(),
            request.studentId(),
            request.sectionId()
        );

        var saved = enrollmentRepository.save(enrollment);
        return new Response(saved.getId());
    }
}
