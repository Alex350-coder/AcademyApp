import { Link } from 'react-router-dom';
import { BookOpen, Users, CalendarCheck, GraduationCap } from 'lucide-react';
import { Card, CardContent } from '@/shared/components/Card';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { useMySections } from '../api/useSections';

export default function TeacherDashboardPage() {
  const { data, isLoading, isError, refetch } = useMySections();

  if (isError) {
    return <ErrorState message="Could not load your dashboard" onRetry={() => refetch()} />;
  }

  const totalSections = data?.length ?? 0;
  const totalStudents = data?.reduce((sum, s) => sum + s.enrolledCount, 0) ?? 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Teacher Dashboard</h1>
        <p className="text-muted text-sm mt-1">Overview of your sections and students</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <Card>
          <CardContent>
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-md bg-primary/10 text-primary">
                <BookOpen className="h-5 w-5" />
              </div>
              <div>
                <p className="text-2xl font-bold text-text">
                  {isLoading ? '—' : totalSections}
                </p>
                <p className="text-sm text-muted">Sections</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent>
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-md bg-primary/10 text-primary">
                <Users className="h-5 w-5" />
              </div>
              <div>
                <p className="text-2xl font-bold text-text">
                  {isLoading ? '—' : totalStudents}
                </p>
                <p className="text-sm text-muted">Students</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent>
            <div className="flex flex-col gap-2">
              <Link
                to="/app/teacher/attendance"
                className="flex items-center gap-2 text-sm text-primary hover:underline"
              >
                <CalendarCheck className="h-4 w-4" /> Take Attendance
              </Link>
              <Link
                to="/app/teacher/grades"
                className="flex items-center gap-2 text-sm text-primary hover:underline"
              >
                <GraduationCap className="h-4 w-4" /> Manage Grades
              </Link>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
