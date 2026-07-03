package com.academicsaas.academic.presentation.controller;

import com.academicsaas.academic.domain.model.Course;
import com.academicsaas.academic.domain.model.Student;
import com.academicsaas.academic.domain.model.valueobject.AttendanceStatus;
import com.academicsaas.academic.domain.repository.CourseRepository;
import com.academicsaas.academic.domain.repository.EvaluationRepository;
import com.academicsaas.academic.domain.repository.StudentRepository;
import com.academicsaas.academic.infrastructure.adapter.AttendanceRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.CourseSectionRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.EnrollmentRepositoryAdapter;
import com.academicsaas.academic.infrastructure.adapter.GradeRepositoryAdapter;
import com.academicsaas.academic.infrastructure.entity.EvaluationTypeJpaEntity;
import com.academicsaas.academic.infrastructure.repository.SpringDataEvaluationTypeRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataStudentRepository;
import com.academicsaas.academic.infrastructure.repository.SpringDataTeacherRepository;
import com.academicsaas.academic.presentation.dto.StudentAttendanceSummaryDto;
import com.academicsaas.academic.presentation.dto.StudentCourseGradesDto;
import com.academicsaas.academic.presentation.dto.StudentDto;
import com.academicsaas.academic.presentation.dto.StudentEvaluationDto;
import com.academicsaas.academic.presentation.dto.StudentListDto;
import com.academicsaas.identity.infrastructure.persistence.entity.UserJpaEntity;
import com.academicsaas.identity.infrastructure.persistence.repository.SpringDataUserRepository;
import com.academicsaas.shared.exception.NotFoundException;
import com.academicsaas.shared.security.CurrentUserContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final EvaluationRepository evaluationRepository;
    private final SpringDataEvaluationTypeRepository evaluationTypeRepository;
    private final CurrentUserContext currentUserContext;

    public StudentController(StudentRepository studentRepository,
                             EnrollmentRepositoryAdapter enrollmentRepository,
                             CourseSectionRepositoryAdapter sectionRepository,
                             GradeRepositoryAdapter gradeRepository,
                             AttendanceRepositoryAdapter attendanceRepository,
                             SpringDataStudentRepository jpaStudentRepository,
                             SpringDataUserRepository userRepository,
                             SpringDataTeacherRepository teacherRepository,
                             CourseRepository courseRepository,
                             EvaluationRepository evaluationRepository,
                             SpringDataEvaluationTypeRepository evaluationTypeRepository,
                             CurrentUserContext currentUserContext) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.sectionRepository = sectionRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
        this.jpaStudentRepository = jpaStudentRepository;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.evaluationRepository = evaluationRepository;
        this.evaluationTypeRepository = evaluationTypeRepository;
        this.currentUserContext = currentUserContext;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR', 'SECRETARY')")
    public ResponseEntity<List<StudentListDto>> getAll(Authentication auth) {
        var institutionId = currentUserContext.institutionId(auth);
        var students = jpaStudentRepository.findAll();
        Map<UUID, UserJpaEntity> userCache = new HashMap<>();
        var result = students.stream()
            .map(s -> Map.entry(s, userCache.computeIfAbsent(s.getUserId(),
                id -> userRepository.findById(id).orElse(null))))
            .filter(entry -> entry.getValue() != null && institutionId.equals(entry.getValue().getInstitutionId()))
            .map(entry -> {
                var s = entry.getKey();
                var user = entry.getValue();
                return new StudentListDto(
                    s.getId(),
                    s.getEnrollmentCode(),
                    (user.getFirstName() + " " + user.getLastName()).trim(),
                    user.getEmail(),
                    s.getGuardianName() != null ? s.getGuardianName() : "",
                    user.getStatus());
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
    public ResponseEntity<List<StudentCourseGradesDto>> getMyCourses(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        return ResponseEntity.ok(buildCourseGradesList(student));
    }

    @GetMapping("/me/grades")
    public ResponseEntity<List<StudentCourseGradesDto>> getMyGrades(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        return ResponseEntity.ok(buildCourseGradesList(student));
    }

    private List<StudentCourseGradesDto> buildCourseGradesList(Student student) {
        var enrollments = enrollmentRepository.findByStudentId(student.getId());
        Map<UUID, String> courseNameCache = new HashMap<>();
        Map<UUID, String> courseCodeCache = new HashMap<>();
        Map<UUID, String> teacherNameCache = new HashMap<>();
        Map<UUID, String> evaluationTypeNameCache = new HashMap<>();

        return enrollments.stream()
            .map(e -> sectionRepository.findById(e.getSectionId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(section -> {
                var courseName = courseNameCache.computeIfAbsent(section.getCourseId(),
                    id -> courseRepository.findById(id).map(Course::getName).orElse("Unknown"));
                var courseCode = courseCodeCache.computeIfAbsent(section.getCourseId(),
                    id -> courseRepository.findById(id).map(Course::getCode).orElse(""));
                var teacherName = teacherNameCache.computeIfAbsent(section.getTeacherId(),
                    id -> teacherRepository.findById(id)
                        .flatMap(t -> userRepository.findById(t.getUserId()))
                        .map(u -> (u.getFirstName() + " " + u.getLastName()).trim())
                        .orElse("Unknown"));

                var grades = gradeRepository.findByStudentIdAndSectionId(student.getId(), section.getId());
                var evaluations = grades.stream()
                    .map(g -> {
                        var evaluation = evaluationRepository.findById(g.getEvaluationId()).orElse(null);
                        var typeName = evaluation == null
                            ? ""
                            : evaluationTypeNameCache.computeIfAbsent(evaluation.getEvaluationTypeId(),
                                id -> evaluationTypeRepository.findById(id)
                                    .map(EvaluationTypeJpaEntity::getName).orElse(""));
                        return new StudentEvaluationDto(
                            g.getId(),
                            evaluation != null ? evaluation.getName() : "Evaluation",
                            g.getScore().value(), g.getScore().maxScore(),
                            evaluation != null ? evaluation.getDate() : null,
                            typeName);
                    })
                    .toList();

                var average = grades.isEmpty()
                    ? BigDecimal.ZERO
                    : grades.stream().map(g -> g.getScore().percentage())
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(grades.size()), 2, RoundingMode.HALF_UP);

                return new StudentCourseGradesDto(
                    section.getId(), courseName, courseCode, teacherName, average, evaluations);
            })
            .toList();
    }

    @GetMapping("/me/attendance")
    public ResponseEntity<List<StudentAttendanceSummaryDto>> getMyAttendance(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        var enrollments = enrollmentRepository.findByStudentId(student.getId());
        Map<UUID, String> courseNameCache = new HashMap<>();

        var result = enrollments.stream()
            .map(e -> sectionRepository.findById(e.getSectionId())
                .map(section -> {
                    var courseName = courseNameCache.computeIfAbsent(section.getCourseId(),
                        id -> courseRepository.findById(id).map(Course::getName).orElse("Unknown"));
                    var records = attendanceRepository.findByEnrollmentIds(List.of(e.getId()));
                    var total = records.size();
                    var present = records.stream()
                        .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                        .count();
                    var percentage = total == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(present).multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
                    return new StudentAttendanceSummaryDto(section.getId(), courseName, present, total, percentage);
                }))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me/schedule")
    public ResponseEntity<List<Map<String, Object>>> getMySchedule(Authentication auth) {
        var userId = UUID.fromString(auth.getName());
        var student = studentRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Student profile for user", userId));
        var enrollments = enrollmentRepository.findByStudentId(student.getId());
        Map<UUID, String> courseNameCache = new HashMap<>();
        Map<UUID, String> courseCodeCache = new HashMap<>();
        Map<UUID, String> teacherNameCache = new HashMap<>();

        // No timetable feature exists yet (CourseSection has no day/time/room
        // fields) - entries intentionally omit dayOfWeek/startTime/endTime/
        // classroom, and the frontend treats that as "not scheduled yet".
        var result = enrollments.stream()
            .map(e -> sectionRepository.findById(e.getSectionId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(section -> {
                var courseName = courseNameCache.computeIfAbsent(section.getCourseId(),
                    id -> courseRepository.findById(id).map(Course::getName).orElse("Unknown"));
                var courseCode = courseCodeCache.computeIfAbsent(section.getCourseId(),
                    id -> courseRepository.findById(id).map(Course::getCode).orElse(""));
                var teacherName = teacherNameCache.computeIfAbsent(section.getTeacherId(),
                    id -> teacherRepository.findById(id)
                        .flatMap(t -> userRepository.findById(t.getUserId()))
                        .map(u -> (u.getFirstName() + " " + u.getLastName()).trim())
                        .orElse("Unknown"));
                return Map.<String, Object>of(
                    "id", section.getId(),
                    "courseName", courseName,
                    "courseCode", courseCode,
                    "teacherName", teacherName);
            })
            .toList();
        return ResponseEntity.ok(result);
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
    public ResponseEntity<StudentDto> getById(@PathVariable UUID id, Authentication auth) {
        var student = studentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Student", id));
        var user = userRepository.findById(student.getUserId()).orElse(null);
        if (user == null || !currentUserContext.institutionId(auth).equals(user.getInstitutionId())) {
            throw new NotFoundException("Student", id);
        }
        return ResponseEntity.ok(toDto(student));
    }

    private StudentDto toDto(Student s) {
        return new StudentDto(s.getId(), s.getUserId(), s.getEnrollmentCode(),
            s.getBirthDate(), s.getGuardianName(), s.getGuardianContact(),
            s.getCreatedAt(), s.getUpdatedAt());
    }
}
