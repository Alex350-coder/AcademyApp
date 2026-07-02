export const teacherEndpoints = {
  mySections: `/api/v1/teachers/me/sections`,
  enrollmentsBySection: (sectionId: string) => `/api/v1/enrollments/section/${sectionId}`,
  attendanceBySection: (sectionId: string, date: string) =>
    `/api/v1/attendance/section/${sectionId}?from=${date}&to=${date}`,
  bulkAttendance: `/api/v1/attendance/bulk`,
  evaluationsBySection: (sectionId: string) => `/api/v1/evaluations/section/${sectionId}`,
  evaluations: `/api/v1/evaluations`,
  evaluationTypes: `/api/v1/evaluations/types`,
  grades: `/api/v1/grades`,
};
