export interface InstitutionalOverview {
  totalStudents: number;
  totalTeachers: number;
  totalActiveSections: number;
  overallAverageScore: number;
  overallAttendanceRate: number;
}

export interface AtRiskStudent {
  studentId: string;
  studentName: string;
  currentAverage: number;
  reason: string;
  sectionId: string;
  sectionName: string;
}

export interface CoursePerformanceData {
  courseId: string;
  courseName: string;
  courseCode: string;
  averageScore: number;
  enrolledStudents: number;
  attendanceRate: number;
}

export interface AttendanceTrendData {
  date: string;
  attendanceRate: number;
  totalRecords: number;
  presentRecords: number;
}

export interface Teacher {
  id: string;
  fullName: string;
  email: string;
  specialty: string;
  hireDate: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface Student {
  id: string;
  enrollmentCode: string;
  fullName: string;
  email: string;
  guardian: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface Course {
  id: string;
  name: string;
  code: string;
  description: string;
  credits: number;
  sectionsCount: number;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface Classroom {
  id: string;
  code: string;
  name: string;
  capacity: number;
  location: string;
}
