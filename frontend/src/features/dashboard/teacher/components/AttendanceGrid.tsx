import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useAttendanceBySection, useBulkAttendance } from '../api/useAttendance';
import { useEnrollmentsBySection } from '../api/useEnrollments';

type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | 'JUSTIFIED';

const statusOptions: { value: AttendanceStatus; label: string; color: 'success' | 'danger' | 'warning' | 'info' }[] = [
  { value: 'PRESENT', label: 'P', color: 'success' },
  { value: 'ABSENT', label: 'A', color: 'danger' },
  { value: 'LATE', label: 'L', color: 'warning' },
  { value: 'JUSTIFIED', label: 'J', color: 'info' },
];

interface AttendanceGridProps {
  sectionId: string;
  date: string;
}

export function AttendanceGrid({ sectionId, date }: AttendanceGridProps) {
  const enrollmentsQuery = useEnrollmentsBySection(sectionId);
  const attendanceQuery = useAttendanceBySection(sectionId, date);
  const bulkMutation = useBulkAttendance();
  const [edits, setEdits] = useState<Record<string, AttendanceStatus>>({});

  const isLoading = enrollmentsQuery.isLoading || attendanceQuery.isLoading;
  const isError = enrollmentsQuery.isError || attendanceQuery.isError;

  const attendanceByStudentId = new Map(
    (attendanceQuery.data ?? []).map((a) => [a.studentId, a]),
  );

  const roster = (enrollmentsQuery.data ?? [])
    .filter((e) => e.status === 'ACTIVE')
    .map((e) => ({
      enrollmentId: e.id,
      studentId: e.studentId,
      studentName: e.studentName,
      status: edits[e.studentId] ?? attendanceByStudentId.get(e.studentId)?.status,
    }));

  function handleStatusChange(studentId: string, status: AttendanceStatus) {
    setEdits((prev) => ({ ...prev, [studentId]: status }));
  }

  function handleKeyDown(e: React.KeyboardEvent, studentId: string) {
    const map: Record<string, AttendanceStatus> = {
      '1': 'PRESENT',
      '2': 'ABSENT',
      '3': 'LATE',
      '4': 'JUSTIFIED',
    };
    const status = map[e.key];
    if (status) {
      e.preventDefault();
      handleStatusChange(studentId, status);
    }
  }

  function handleSaveAll() {
    const payload = {
      sectionId,
      date,
      attendances: roster
        .filter((r) => r.status)
        .map((r) => ({ enrollmentId: r.enrollmentId, status: r.status as AttendanceStatus })),
    };
    bulkMutation.mutate(payload, {
      onSuccess: () => setEdits({}),
    });
  }

  if (!sectionId || !date) {
    return (
      <EmptyState
        title="Select a section and date"
        description="Choose a section and date to start recording attendance"
      />
    );
  }

  if (isError) {
    return (
      <ErrorState
        message="Could not load attendance data"
        onRetry={() => {
          enrollmentsQuery.refetch();
          attendanceQuery.refetch();
        }}
      />
    );
  }

  if (isLoading) {
    return (
      <Card>
        <CardContent>
          <div className="space-y-3 animate-pulse">
            {Array.from({ length: 5 }).map((_, i) => (
              <div key={i} className="h-12 bg-surface-hover rounded" />
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  if (roster.length === 0) {
    return (
      <EmptyState
        title="No students enrolled"
        description="This section has no enrolled students"
      />
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Attendance - {date}</CardTitle>
        <div className="flex items-center gap-3">
          <div className="flex gap-2 text-xs text-muted">
            {statusOptions.map((opt) => (
              <span key={opt.value} className="flex items-center gap-1">
                <Badge variant={opt.color}>{opt.label}</Badge> {opt.value}
              </span>
            ))}
          </div>
          <Button
            size="sm"
            onClick={handleSaveAll}
            loading={bulkMutation.isPending}
            disabled={Object.keys(edits).length === 0}
          >
            Save All
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border">
                <th className="px-4 py-2 text-left text-muted font-medium">Student</th>
                {statusOptions.map((opt) => (
                  <th key={opt.value} className="px-4 py-2 text-center text-muted font-medium w-20">
                    {opt.label}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {roster.map((record) => (
                <tr
                  key={record.studentId}
                  className="border-b border-border hover:bg-surface-hover transition-colors"
                  onKeyDown={(e) => handleKeyDown(e, record.studentId)}
                  tabIndex={0}
                >
                  <td className="px-4 py-3 text-text">{record.studentName}</td>
                  {statusOptions.map((opt) => (
                    <td key={opt.value} className="px-4 py-3 text-center">
                      <input
                        type="radio"
                        name={`status-${record.studentId}`}
                        checked={record.status === opt.value}
                        onChange={() => handleStatusChange(record.studentId, opt.value)}
                        className="accent-primary cursor-pointer"
                      />
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  );
}
