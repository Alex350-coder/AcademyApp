import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { useMyProfile } from '../api/useStudentData';
import { Button } from '@/shared/components/Button';
import { ProgressSummaryCard } from '../components/ProgressSummaryCard';
import { UpcomingClassWidget } from '../components/UpcomingClassWidget';
import { ObservationsPanel } from '../components/ObservationsPanel';

export default function StudentDashboardPage() {
  const navigate = useNavigate();
  const { data: profile } = useMyProfile();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">
          Welcome, {profile?.fullName ?? 'Student'}
        </h1>
        <p className="text-muted text-sm mt-1">Track your academic progress and schedule</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <ProgressSummaryCard />
        <UpcomingClassWidget />
        <ObservationsPanel />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Quick Links</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <Button
              variant="secondary"
              onClick={() => navigate('/student/courses')}
              className="flex flex-col items-center justify-center p-4"
            >
              <span className="text-2xl mb-1">📚</span>
              <span className="text-xs font-medium">My Courses</span>
            </Button>
            <Button
              variant="secondary"
              onClick={() => navigate('/student/grades')}
              className="flex flex-col items-center justify-center p-4"
            >
              <span className="text-2xl mb-1">📊</span>
              <span className="text-xs font-medium">Grades</span>
            </Button>
            <Button
              variant="secondary"
              onClick={() => navigate('/student/attendance')}
              className="flex flex-col items-center justify-center p-4"
            >
              <span className="text-2xl mb-1">✅</span>
              <span className="text-xs font-medium">Attendance</span>
            </Button>
            <Button
              variant="secondary"
              onClick={() => navigate('/student/schedule')}
              className="flex flex-col items-center justify-center p-4"
            >
              <span className="text-2xl mb-1">📅</span>
              <span className="text-xs font-medium">Schedule</span>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
