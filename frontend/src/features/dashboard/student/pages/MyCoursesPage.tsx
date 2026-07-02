import { Card, CardContent } from '@/shared/components/Card';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMyCourses } from '../api/useStudentData';

export default function MyCoursesPage() {
  const { data, isLoading, isError, refetch } = useMyCourses();

  if (isError) {
    return <ErrorState message="Could not load your courses" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">My Courses</h1>
        <p className="text-muted text-sm mt-1">View your enrolled courses and sections</p>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <Card key={i}>
              <CardContent>
                <div className="space-y-2 animate-pulse">
                  <div className="h-5 w-40 bg-surface-hover rounded" />
                  <div className="h-4 w-24 bg-surface-hover rounded" />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : data && data.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {data.map((course) => (
            <Card key={course.sectionId}>
              <CardContent>
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-text">{course.courseName}</h3>
                    <p className="text-sm text-muted">{course.courseCode}</p>
                  </div>
                  <Badge variant={course.average >= 70 ? 'success' : course.average >= 60 ? 'warning' : 'danger'}>
                    {course.average.toFixed(1)}%
                  </Badge>
                </div>
                <div className="mt-3 text-sm text-muted">
                  <p className="flex items-center gap-2">
                    <span>Teacher:</span>
                    <span className="text-text">{course.teacherName}</span>
                  </p>
                  {course.evaluations.length > 0 && (
                    <p className="flex items-center gap-2 mt-1">
                      <span>Evaluations:</span>
                      <span className="text-text">{course.evaluations.length}</span>
                    </p>
                  )}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No courses enrolled"
          description="You are not currently enrolled in any courses"
          action={{ label: 'View Schedule', onClick: () => window.location.href = '/student/schedule' }}
        />
      )}
    </div>
  );
}
