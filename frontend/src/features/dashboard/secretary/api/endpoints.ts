export const secretaryEndpoints = {
  availableSections: `/api/v1/sections/available`,
  enrollments: `/api/v1/enrollments`,
  bulkAttendance: `/api/v1/attendance/bulk`,
  students: `/api/v1/students`,
  pendingTasks: `/api/v1/enrollments/pending-tasks`,
  sections: `/api/v1/sections`,
  attendanceBySection: (sectionId: string, date: string) =>
    `/api/v1/attendance/section/${sectionId}?date=${date}`,
  enrollmentById: (id: string) => `/api/v1/enrollments/${id}`,
};
