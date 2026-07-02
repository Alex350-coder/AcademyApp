import { useMemo } from 'react';
import { Card, CardContent } from '@/shared/components/Card';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMySchedule } from '../api/useStudentData';
import type { ScheduleEntry } from '../api/types';

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
export default function MySchedulePage() {
  const { data, isLoading, isError, refetch } = useMySchedule();

  const scheduleMap = useMemo(() => {
    const map: Record<string, ScheduleEntry[]> = {};
    if (!data) return map;
    data.forEach((entry) => {
      if (!map[entry.dayOfWeek]) map[entry.dayOfWeek] = [];
      map[entry.dayOfWeek].push(entry);
    });
    return map;
  }, [data]);

  if (isError) {
    return <ErrorState message="Could not load your schedule" onRetry={() => refetch()} />;
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-text">My Schedule</h1>
          <p className="text-muted text-sm mt-1">Weekly class schedule</p>
        </div>
        <Card>
          <CardContent>
            <div className="space-y-4 animate-pulse">
              {Array.from({ length: 5 }).map((_, i) => (
                <div key={i} className="h-24 bg-surface-hover rounded" />
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-text">My Schedule</h1>
          <p className="text-muted text-sm mt-1">Weekly class schedule</p>
        </div>
        <EmptyState
          title="No classes scheduled"
          description="Your schedule is empty. Enroll in courses to see your schedule here."
        />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">My Schedule</h1>
        <p className="text-muted text-sm mt-1">Weekly class schedule</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
        {DAYS.map((day) => {
          const dayClasses = scheduleMap[day] ?? [];
          dayClasses.sort((a, b) => a.startTime.localeCompare(b.startTime));

          return (
            <Card key={day} className="min-h-[200px]">
              <CardContent>
                <h3 className="text-sm font-semibold text-text mb-3 text-center uppercase tracking-wider">
                  {day.charAt(0) + day.slice(1).toLowerCase().slice(0, 3)}
                </h3>
                {dayClasses.length > 0 ? (
                  <div className="space-y-2">
                    {dayClasses.map((entry) => (
                      <div
                        key={entry.id}
                        className="p-2 rounded-md bg-primary/5 border border-primary/20"
                      >
                        <p className="text-xs font-medium text-text leading-tight">{entry.courseName}</p>
                        <p className="text-[10px] text-muted mt-0.5">
                          {entry.startTime} - {entry.endTime}
                        </p>
                        <p className="text-[10px] text-muted">{entry.classroom}</p>
                        <Badge variant="default" className="mt-1 text-[10px] px-1.5 py-0">
                          {entry.courseCode}
                        </Badge>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-xs text-muted text-center mt-8">No classes</p>
                )}
              </CardContent>
            </Card>
          );
        })}
      </div>

      <Card>
        <CardContent>
          <h3 className="text-sm font-semibold text-text mb-2">Detailed Schedule</h3>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border">
                  <th className="px-3 py-2 text-left text-muted font-medium">Day</th>
                  <th className="px-3 py-2 text-left text-muted font-medium">Course</th>
                  <th className="px-3 py-2 text-left text-muted font-medium">Time</th>
                  <th className="px-3 py-2 text-left text-muted font-medium">Classroom</th>
                  <th className="px-3 py-2 text-left text-muted font-medium">Teacher</th>
                </tr>
              </thead>
              <tbody>
                {data
                  .sort((a, b) => {
                    const dayDiff = DAYS.indexOf(a.dayOfWeek) - DAYS.indexOf(b.dayOfWeek);
                    if (dayDiff !== 0) return dayDiff;
                    return a.startTime.localeCompare(b.startTime);
                  })
                  .map((entry) => (
                    <tr
                      key={entry.id}
                      className="border-b border-border last:border-b-0 hover:bg-surface-hover transition-colors"
                    >
                      <td className="px-3 py-2 text-text">
                        {entry.dayOfWeek.charAt(0) + entry.dayOfWeek.slice(1).toLowerCase()}
                      </td>
                      <td className="px-3 py-2">
                        <span className="font-medium text-text">{entry.courseName}</span>
                        <span className="text-muted ml-1">({entry.courseCode})</span>
                      </td>
                      <td className="px-3 py-2 text-muted">
                        {entry.startTime} - {entry.endTime}
                      </td>
                      <td className="px-3 py-2 text-muted">{entry.classroom}</td>
                      <td className="px-3 py-2 text-muted">{entry.teacherName}</td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
