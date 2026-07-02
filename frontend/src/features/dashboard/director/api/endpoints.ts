export const directorEndpoints = {
  overview: `/api/v1/reports/institutional-overview`,
  atRiskStudents: `/api/v1/reports/at-risk-students`,
  coursePerformance: `/api/v1/reports/course-performance`,
  attendanceTrend: `/api/v1/reports/attendance-trend`,
  teachers: `/api/v1/teachers`,
  teacherById: (id: string) => `/api/v1/teachers/${id}`,
  students: `/api/v1/students`,
  studentById: (id: string) => `/api/v1/students/${id}`,
  courses: `/api/v1/courses`,
  courseById: (id: string) => `/api/v1/courses/${id}`,
  classrooms: `/api/v1/classrooms`,
  classroomById: (id: string) => `/api/v1/classrooms/${id}`,
};
