import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { PendingTasksWidget } from '../components/PendingTasksWidget';
import { Button } from '@/shared/components/Button';

export default function SecretaryDashboardPage() {
  const navigate = useNavigate();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Secretary Dashboard</h1>
        <p className="text-muted text-sm mt-1">Manage enrollments, attendance, and student records</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <PendingTasksWidget />

        <Card className="md:col-span-2">
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Button
                variant="secondary"
                onClick={() => navigate('/app/secretary/enrollments')}
                className="flex flex-col items-center justify-center p-6 text-center"
              >
                <span className="text-3xl mb-2">📋</span>
                <span className="text-sm font-medium">Enrollment Wizard</span>
                <span className="text-xs text-muted mt-1">Enroll students in sections</span>
              </Button>
              <Button
                variant="secondary"
                onClick={() => navigate('/app/secretary/attendance')}
                className="flex flex-col items-center justify-center p-6 text-center"
              >
                <span className="text-3xl mb-2">✅</span>
                <span className="text-sm font-medium">Attendance Registry</span>
                <span className="text-xs text-muted mt-1">Register daily attendance</span>
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
