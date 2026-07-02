import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { usePendingTasks } from '../api/useEnrollments';

export function PendingTasksWidget() {
  const navigate = useNavigate();
  const { data, isLoading, isError, refetch } = usePendingTasks();

  if (isError) {
    return <ErrorState message="Could not load pending tasks" onRetry={() => refetch()} />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Pending Tasks</CardTitle>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-3 animate-pulse">
            <div className="h-12 bg-surface-hover rounded" />
            <div className="h-12 bg-surface-hover rounded" />
          </div>
        ) : data ? (
          <div className="space-y-4">
            <div className="flex items-center justify-between p-3 rounded-md bg-surface-hover">
              <div>
                <p className="text-sm font-medium text-text">Pending Enrollments</p>
                <p className="text-xs text-muted">Awaiting confirmation</p>
              </div>
              <span className="text-2xl font-bold text-warning">{data.pendingEnrollments ?? 0}</span>
            </div>
            <div className="flex items-center justify-between p-3 rounded-md bg-surface-hover">
              <div>
                <p className="text-sm font-medium text-text">Unregistered Attendance</p>
                <p className="text-xs text-muted">Missing today&apos;s entry</p>
              </div>
              <span className="text-2xl font-bold text-danger">{data.unregisteredAttendance ?? 0}</span>
            </div>
            <div className="flex gap-2 pt-2">
              <Button
                variant="primary"
                size="sm"
                className="flex-1"
                onClick={() => navigate('/secretary/enrollments')}
              >
                Go to Enrollments
              </Button>
              <Button
                variant="secondary"
                size="sm"
                className="flex-1"
                onClick={() => navigate('/secretary/attendance')}
              >
                Register Attendance
              </Button>
            </div>
          </div>
        ) : (
          <p className="text-sm text-muted text-center py-4">No pending tasks</p>
        )}
      </CardContent>
    </Card>
  );
}
