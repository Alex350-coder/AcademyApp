package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.domain.model.Student;
import com.academicsaas.academic.domain.model.valueobject.AttendanceStatus;
import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.domain.repository.StudentRepository;
import com.academicsaas.academic.infrastructure.adapter.AttendanceRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.CourseSectionRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.EnrollmentRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.GradeRepositoryAdapter;
import com.academicsaas.academic.infrastructure.repository.SpringDataStudentRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataTeacherRepository;
import com.academicsaas.academic.presentation.dto.AttendanceDto;
import com.academicsaas.academic.presentation.dto.SectionDto;
import com.academicsaas.academic.presentation.dto.StudentDto;
import com.academicsaas.academic.presentation.dto.StudentGradesDto;
import com.academicsaas.academic.presentation.dto.StudentListDto;
import com.academicsaas.identity.infrastructure.persistence.entity.UserJpaEntity;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.shared.exception.NotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final EnrollmentRepositoryAdapter enrollmentRepository;
    private final CourseSectionRepositoryAdapter sectionRepository;
    private final GradeRepositoryAdapter gradeRepository;
    private final AttendanceRepositoryAdapter attendanceRepository;
    private final SpringDataStudentRepository jpaStudentRepository;
    private final SpringDataUserRepository userRepository;
    private final SpringDataTeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    public StudentController(StudentRepository studentRepository,
                             EnrollmentRepositoryAdapter enrollmentRepository,
                             CourseSectionRepositoryAdapter sectionRepository,
                             GradeRepositoryAdapter gradeRepository,
                             AttendanceRepositoryAdapter attendanceRepository,
                             SpringDataStudentRepository jpaStudentRepository,
                             SpringDataUserRepository userRepository,
                             SpringDataTeacherRepository teacherRepository,
                             CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.sectionRepository = sectionRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
        this.jpaStudentRepository = jpaStudentRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR', 'SECRETARY')")
    public ResponseEntity<List<StudentListDto>> getAll() {
        var students = jpaStudentRepository.findAll();
        Map<UUID, UserJpaEntity> userCache = new HashMap<>();
        var result = students.stream().map(s -> {
            var user = userCache.computeIfAbsent(s.getUserId(),
                id -> userRepository.findById(id).orElse(null));
            return new StudentListDto(
                s.getId(),
                s.getEnrollmentCode(),
                user != null ? (user.getFirstName() + " " + user.getLastName()).trim() : "Unknown",
                user != null ? user.getEmail() : "unknown@email.com",
                s.getGuardianName() != null ? s.getGuardianName() : "",
                user != null ? user.getStatus() : "INACTIVE");
        }).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        var user = userRepository.findById(student.getUserId()).orElse(null);

        var enrollments = enrollmentRepository.findByStudentId(student.getId());
        var allGrades = enrollments.stream()
            .flatMap(e -> gradeRepository.findByStudentIdAndSectionId(student.getId(), e.getSectionId()).stream())
            .toList();
        var overallAverage = allGrades.isEmpty()
            ? BigDecimal.ZERO
            : allGrades.stream()
                .map(g -> g.getScore().percentage())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(allGrades.size()), 2, RoundingMode.HALF_UP);

        var enrollmentIds = enrollments.stream().map(e -> e.getId()).toList();
        var attendanceRecords = attendanceRepository.findByEnrollmentIds(enrollmentIds);
        var overallAttendance = attendanceRecords.isEmpty()
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(attendanceRecords.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                    .count())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(attendanceRecords.size()), 2, RoundingMode.HALF_UP);

        return ResponseEntity.ok(Map.of(
            "id", student.getId(),
            "enrollmentCode", student.getEnrollmentCode(),
            "fullName", user != null ? (user.getFirstName() + " " + user.getLastName()).trim() : "Unknown",
            "email", user != null ? user.getEmail() : "unknown@email.com",
            "overallAverage", overallAverage,
            "overallAttendance", overallAttendance));
    }

    @GetMapping("/me/courses")
    public ResponseEntity<List<SectionDto>> getMyCourses(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        var enrollments = enrollmentRepository.findByStudentId(student.getId());
        Map<UUID, String> courseNameCache = new HashMap<>();
        Map<UUID, String> teacherNameCache = new HashMap<>();
        var sections = enrollments.stream()
            .map(e -> sectionRepository.findById(e.getSectionId()))
            .filter(opt -> opt.isPresent())
            .map(opt -> {
                var section = opt.get();
                var courseName = courseNameCache.computeIfAbsent(section.getCourseId(),
                    id -> courseRepository.findById(id).map(c -> c.getName()).orElse(null));
                var teacherName = teacherNameCache.computeIfAbsent(section.getTeacherId(),
                    id -> teacherRepository.findById(id)
                        .flatMap(t -> userRepository.findById(t.getUserId()))
                        .map(u -> (u.getFirstName() + " " + u.getLastName()).trim())
                        .orElse(null));
                return new SectionDto(
                    section.getId(), section.getCourseId(), section.getAcademicPeriodId(),
                    section.getTeacherId(), section.getClassroomId(),
                    section.getName(), section.getCapacity(), section.getEnrolledCount(),
                    courseName, teacherName,
                    section.getCreatedAt(), section.getUpdatedAt());
            })
            .toList();
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/me/grades")
    public ResponseEntity<List<StudentGradesDto>> getMyGrades(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        var enrollments = enrollmentRepository.findByStudentId(student.getId());
        var result = enrollments.stream()
            .map(e -> {
                var secId = e.getSectionId();
                var grades = gradeRepository.findByStudentIdAndSectionId(student.getId(), secId);
                return new StudentGradesDto(
                    student.getId(), secId, "Section-" + secId.toString().substring(0, 8),
                    null,
                    grades.stream().map(g -> new com.academicsaas.academic.presentation.dto.GradeDto(
                        g.getId(), g.getEvaluationId(), g.getStudentId(),
                        g.getScore().value(), g.getScore().maxScore(),
                        g.getComments(), g.getGradedBy(), g.getGradedAt(),
                        g.getCreatedAt(), g.getUpdatedAt())).toList());
            })
            .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me/attendance")
    public ResponseEntity<List<AttendanceDto>> getMyAttendance(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        var studentUser = userRepository.findById(student.getUserId()).orElse(null);
        var studentName = studentUser != null
            ? (studentUser.getFirstName() + " " + studentUser.getLastName()).trim()
            : null;
        var enrollments = enrollmentRepository.findByStudentId(student.getId());
        var enrollmentIds = enrollments.stream().map(e -> e.getId()).toList();
        var records = attendanceRepository.findByEnrollmentIds(enrollmentIds);
        return ResponseEntity.ok(records.stream()
            .map(a -> new AttendanceDto(a.getId(), a.getEnrollmentId(), student.getId(), studentName, a.getDate(),
                a.getStatus().name(), a.getJustification(),
                a.getCreatedAt(), a.getUpdatedAt()))
            .toList());
    }

    @GetMapping("/me/schedule")
    public ResponseEntity<List<SectionDto>> getMySchedule(Authentication auth) {
        return getMyCourses(auth);
    }

    @GetMapping("/me/observations")
    public ResponseEntity<Map<String, Object>> getMyObservations(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        var enrollments = enrollmentRepository.findByStudentId(student.getId());
        var observation = "Student " + student.getEnrollmentCode()
            + " is enrolled in " + enrollments.size() + " sections.";
        return ResponseEntity.ok(Map.of(
            "studentId", student.getId(),
            "enrollmentCode", student.getEnrollmentCode(),
            "totalSections", enrollments.size(),
            "observation", observation));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<StudentDto> getById(@PathVariable UUID id) {
        var student = studentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Student", id));
        return ResponseEntity.ok(toDto(student));
    }

    private StudentDto toDto(Student s) {
        return new StudentDto(s.getId(), s.getUserId(), s.getEnrollmentCode(),
            s.getBirthDate(), s.getGuardianName(), s.getGuardianContact(),
            s.getCreatedAt(), s.getUpdatedAt());
    }
}
