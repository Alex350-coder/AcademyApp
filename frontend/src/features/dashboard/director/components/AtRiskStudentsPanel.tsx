import { useMemo } from 'react';
import { motion } from 'framer-motion';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useAtRiskStudents } from '../api/useAtRiskStudents';

// Literal hex (not CSS vars): getSeverityBg() below does string-concatenation alpha
// blending ("${color}1A"), which requires a plain hex string, not var(...).
function getSeverityColor(avg: number): string {
  if (avg < 55) return '#EF4444'; // matches --color-danger
  if (avg <= 60) return '#f97316'; // intermediate severity tier, no matching token
  return '#F59E0B'; // matches --color-warning
}

function getInitials(name: string): string {
  return name
    .split(' ')
    .map((w) => w[0])
    .filter(Boolean)
    .slice(0, 2)
    .join('')
    .toUpperCase();
}

function getSeverityBg(color: string): string {
  return `${color}1A`;
}

export function AtRiskStudentsPanel() {
  const { data, isLoading, isError, refetch } = useAtRiskStudents();

  const sorted = useMemo(() => {
    if (!data) return [];
    return [...data].sort((a, b) => a.currentAverage - b.currentAverage);
  }, [data]);

  if (isError) {
    return <ErrorState message="Could not load at-risk students" onRetry={() => refetch()} />;
  }

  return (
    <Card className="flex flex-col h-full border-l-[3px] border-l-danger">
      <CardHeader>
        <CardTitle>At-Risk Students</CardTitle>
        {!isLoading && data && (
          <span className="inline-flex items-center gap-1.5 text-sm font-medium text-danger">
            <motion.span
              className="inline-block h-2 w-2 rounded-full bg-danger"
              animate={{ opacity: [1, 0.3, 1] }}
              transition={{ duration: 2, repeat: Infinity }}
            />
            {data.length} student{data.length !== 1 ? 's' : ''}
          </span>
        )}
      </CardHeader>
      <CardContent className="flex-1 flex flex-col">
        {isLoading ? (
          <div className="space-y-3 animate-pulse flex-1">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="h-16 bg-surface-hover rounded" />
            ))}
          </div>
        ) : sorted.length > 0 ? (
          <div className="flex-1 space-y-3 overflow-y-auto">
            {sorted.map((student, index) => {
              const color = getSeverityColor(student.currentAverage);
              return (
                <motion.div
                  key={student.studentId}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ duration: 0.3, delay: index * 0.05 }}
                  className="flex items-center gap-3 p-3 rounded-md bg-surface-hover"
                >
                  <div
                    className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-xs font-bold"
                    style={{ backgroundColor: getSeverityBg(color), color }}
                  >
                    {getInitials(student.studentName)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-text truncate">{student.studentName}</p>
                    <p className="text-xs text-muted mt-0.5">{student.reason}</p>
                    <p className="text-xs text-muted">{student.sectionName}</p>
                  </div>
                  <span
                    className="text-xs font-semibold px-2.5 py-1 rounded-full shrink-0"
                    style={{
                      backgroundColor: getSeverityBg(color),
                      color,
                    }}
                  >
                    {student.currentAverage.toFixed(1)}%
                  </span>
                </motion.div>
              );
            })}
          </div>
        ) : (
          <div className="flex-1 flex items-center justify-center">
            <EmptyState
              title="No at-risk students"
              description="All students are performing well"
            />
          </div>
        )}
      </CardContent>
    </Card>
  );
}
