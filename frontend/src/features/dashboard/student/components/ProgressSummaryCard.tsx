import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { useMyProfile } from '../api/useStudentData';

function getTrendIcon(current: number, previous?: number) {
  if (previous === undefined) return null;
  if (current > previous) return <span className="text-success text-lg">↑</span>;
  if (current < previous) return <span className="text-danger text-lg">↓</span>;
  return <span className="text-muted text-lg">→</span>;
}

export function ProgressSummaryCard() {
  const { data, isLoading, isError, refetch } = useMyProfile();

  if (isError) {
    return <ErrorState message="Could not load progress" onRetry={() => refetch()} />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Overall Progress</CardTitle>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-3 animate-pulse">
            <div className="h-16 w-32 bg-surface-hover rounded mx-auto" />
            <div className="h-4 w-24 bg-surface-hover rounded mx-auto" />
          </div>
        ) : data ? (
          <div className="text-center">
            <div className="flex items-center justify-center gap-2">
              <span className="text-5xl font-bold text-text">{data.overallAverage.toFixed(1)}</span>
              {getTrendIcon(data.overallAverage)}
            </div>
            <p className="text-sm text-muted mt-1">Overall Average</p>
            <div className="mt-4 flex justify-center gap-4">
              <div>
                <Badge variant={data.overallAttendance >= 80 ? 'success' : data.overallAttendance >= 60 ? 'warning' : 'danger'}>
                  {data.overallAttendance.toFixed(1)}%
                </Badge>
                <p className="text-xs text-muted mt-1">Attendance</p>
              </div>
              <div>
                <Badge variant="info">{data.enrollmentCode}</Badge>
                <p className="text-xs text-muted mt-1">Code</p>
              </div>
            </div>
          </div>
        ) : (
          <p className="text-sm text-muted text-center py-4">No progress data available</p>
        )}
      </CardContent>
    </Card>
  );
}
