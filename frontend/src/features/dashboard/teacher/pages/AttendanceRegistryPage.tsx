import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { AttendanceGrid } from '../components/AttendanceGrid';
import { useMySections } from '../api/useSections';

export default function AttendanceRegistryPage() {
  const [searchParams] = useSearchParams();
  const [selectedSectionId, setSelectedSectionId] = useState(searchParams.get('sectionId') ?? '');
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const { data: sections, isLoading, isError, refetch } = useMySections();

  if (isError) {
    return <ErrorState message="Could not load your sections" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Attendance Registry</h1>
        <p className="text-muted text-sm mt-1">Register and manage daily attendance for your sections</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Select Section & Date</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-end gap-4">
            <div className="flex-1">
              <label className="text-sm font-medium text-text block mb-1">Section</label>
              {isLoading ? (
                <div className="h-10 bg-surface-hover rounded animate-pulse" />
              ) : sections && sections.length > 0 ? (
                <select
                  value={selectedSectionId}
                  onChange={(e) => setSelectedSectionId(e.target.value)}
                  className="w-full px-3 py-2 rounded-md bg-surface text-text border border-border focus:outline-none focus:ring-2 focus:ring-[var(--color-border-focus)]"
                >
                  <option value="">Select a section...</option>
                  {sections.map((section) => (
                    <option key={section.id} value={section.id}>
                      {section.courseName ?? 'Untitled Course'} - {section.name}
                    </option>
                  ))}
                </select>
              ) : (
                <p className="text-sm text-muted">No sections assigned</p>
              )}
            </div>
            <div>
              <label className="text-sm font-medium text-text block mb-1">Date</label>
              <input
                type="date"
                value={selectedDate}
                onChange={(e) => setSelectedDate(e.target.value)}
                className="px-3 py-2 rounded-md bg-surface text-text border border-border focus:outline-none focus:ring-2 focus:ring-[var(--color-border-focus)]"
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {selectedSectionId ? (
        <AttendanceGrid sectionId={selectedSectionId} date={selectedDate} />
      ) : (
        <EmptyState
          title="Select a section"
          description="Choose a section from the dropdown above to register attendance"
        />
      )}
    </div>
  );
}
