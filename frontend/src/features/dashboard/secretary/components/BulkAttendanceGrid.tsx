import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useAttendanceBySection, useBulkAttendance } from '../api/useAttendance';


type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | 'JUSTIFIED';

const statusOptions: { value: AttendanceStatus; label: string; color: 'success' | 'danger' | 'warning' | 'info' }[] = [
  { value: 'PRESENT', label: 'P', color: 'success' },
  { value: 'ABSENT', label: 'A', color: 'danger' },
  { value: 'LATE', label: 'L', color: 'warning' },
  { value: 'JUSTIFIED', label: 'J', color: 'info' },
];

interface BulkAttendanceGridProps {
  sectionId: string;
  date: string;
}

export function BulkAttendanceGrid({ sectionId, date }: BulkAttendanceGridProps) {
  const { data, isLoading, isError, refetch } = useAttendanceBySection(sectionId, date);
  const bulkMutation = useBulkAttendance();
  const [records, setRecords] = useState<Record<string, AttendanceStatus>>({});

  const currentRecords =
    Object.keys(records).length > 0
      ? data?.map((r) => ({
          ...r,
          status: records[r.studentId] ?? r.status,
        }))
      : data;

  function handleStatusChange(studentId: string, status: AttendanceStatus) {
    setRecords((prev) => ({ ...prev, [studentId]: status }));
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
      records: Object.entries(records).map(([studentId, status]) => ({
        studentId,
        status,
      })),
    };
    bulkMutation.mutate(payload);
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
    return <ErrorState message="Could not load attendance data" onRetry={() => refetch()} />;
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

  if (!currentRecords || currentRecords.length === 0) {
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
            disabled={Object.keys(records).length === 0}
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
              {currentRecords.map((record) => {
                const currentStatus = records[record.studentId] ?? record.status;
                return (
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
                          checked={currentStatus === opt.value}
                          onChange={() => handleStatusChange(record.studentId, opt.value)}
                          className="accent-primary cursor-pointer"
                        />
                      </td>
                    ))}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  );
}
