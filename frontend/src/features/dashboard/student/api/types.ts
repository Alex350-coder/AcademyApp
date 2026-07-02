export interface StudentProfile {
  id: string;
  enrollmentCode: string;
  fullName: string;
  email: string;
  overallAverage: number;
  overallAttendance: number;
}

export interface CourseWithGrades {
  sectionId: string;
  courseName: string;
  courseCode: string;
  teacherName: string;
  average: number;
  evaluations: Evaluation[];
}

export interface Evaluation {
  id: string;
  name: string;
  score: number;
  maxScore: number;
  date: string;
  type: string;
}

export interface AttendanceSummary {
  sectionId: string;
  courseName: string;
  presentCount: number;
  totalCount: number;
  percentage: number;
}

export interface ScheduleEntry {
  id: string;
  courseName: string;
  courseCode: string;
  teacherName: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  classroom: string;
}

export interface Observation {
  id: string;
  title: string;
  message: string;
  date: string;
  type: 'INFO' | 'WARNING' | 'SUCCESS';
}
