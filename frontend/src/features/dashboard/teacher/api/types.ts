export interface Section {
  id: string;
  courseId: string;
  name: string;
  courseName: string;
  teacherName: string;
  capacity: number;
  enrolledCount: number;
}

export interface SectionEnrollment {
  id: string;
  studentId: string;
  studentName: string;
  sectionId: string;
  status: 'ACTIVE' | 'CANCELLED';
}

export interface AttendanceRecord {
  enrollmentId: string;
  studentId: string;
  studentName: string;
  status: 'PRESENT' | 'ABSENT' | 'LATE' | 'JUSTIFIED';
}

export interface EvaluationType {
  id: string;
  name: string;
  weightPercentage: number;
}

export interface Evaluation {
  id: string;
  sectionId: string;
  evaluationTypeId: string;
  evaluationTypeName: string;
  name: string;
  date: string | null;
  maxScore: number;
}

export interface Grade {
  id: string;
  evaluationId: string;
  studentId: string;
  score: number;
  maxScore: number;
  comments: string | null;
}
