export interface Section {
  id: string;
  code: string;
  courseName: string;
  teacherName: string;
  schedule: string;
  capacity: number;
  enrolledCount: number;
  available: boolean;
}

export interface Enrollment {
  id: string;
  studentId: string;
  studentName: string;
  sectionId: string;
  sectionName: string;
  enrollmentDate: string;
  status: 'ACTIVE' | 'CANCELLED';
}

export interface AttendanceRecord {
  studentId: string;
  studentName: string;
  status: 'PRESENT' | 'ABSENT' | 'LATE' | 'JUSTIFIED';
}

export interface Student {
  id: string;
  enrollmentCode: string;
  fullName: string;
  email: string;
}

export interface PendingTasks {
  pendingEnrollments: number;
  unregisteredAttendance: number;
  pendingCount: number;
}
