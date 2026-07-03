import { useMemo } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMySchedule } from '../api/useStudentData';

const DAYS = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];

export function UpcomingClassWidget() {
  const { data, isLoading, isError, refetch } = useMySchedule();

  const nextClass = useMemo(() => {
    // dayOfWeek/startTime aren't populated by the backend yet (no timetable
    // feature exists) - skip entries missing that data instead of crashing.
    const scheduled = (data ?? []).filter((s) => s.dayOfWeek && s.startTime);
    if (scheduled.length === 0) return null;
    const now = new Date();
    const currentDay = DAYS[now.getDay()];
    const currentTime = now.getHours() * 60 + now.getMinutes();

    const sorted = [...scheduled].sort((a, b) => {
      const dayOrder = DAYS.indexOf(a.dayOfWeek) - DAYS.indexOf(b.dayOfWeek);
      if (dayOrder !== 0) return dayOrder;
      return a.startTime.localeCompare(b.startTime);
    });

    const todayClasses = sorted.filter((s) => s.dayOfWeek === currentDay);
    const upcoming = todayClasses.find((s) => {
      const [h, m] = s.startTime.split(':').map(Number);
      return h * 60 + m > currentTime;
    });

    if (upcoming) return upcoming;

    const nextDay = sorted.find((s) => DAYS.indexOf(s.dayOfWeek) > DAYS.indexOf(currentDay));
    return nextDay ?? sorted[0];
  }, [data]);

  if (isError) {
    return <ErrorState message="Could not load schedule" onRetry={() => refetch()} />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Upcoming Class</CardTitle>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-2 animate-pulse">
            <div className="h-5 w-40 bg-surface-hover rounded" />
            <div className="h-4 w-32 bg-surface-hover rounded" />
          </div>
        ) : nextClass ? (
          <div>
            <p className="text-lg font-semibold text-text">{nextClass.courseName}</p>
            <p className="text-sm text-muted mt-1">{nextClass.courseCode}</p>
            <p className="text-sm text-muted">
              {nextClass.dayOfWeek.charAt(0) + nextClass.dayOfWeek.slice(1).toLowerCase()}{' '}
              {nextClass.startTime} - {nextClass.endTime}
            </p>
            <p className="text-sm text-muted">{nextClass.classroom}</p>
            <p className="text-xs text-muted mt-2">Teacher: {nextClass.teacherName}</p>
          </div>
        ) : (
          <EmptyState title="No classes scheduled" description="Your schedule is empty" />
        )}
      </CardContent>
    </Card>
  );
}
