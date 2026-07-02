import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMyObservations } from '../api/useStudentData';

const typeVariants: Record<string, 'info' | 'warning' | 'success'> = {
  INFO: 'info',
  WARNING: 'warning',
  SUCCESS: 'success',
};

export function ObservationsPanel() {
  const { data, isLoading, isError, refetch } = useMyObservations();

  if (isError) {
    return <ErrorState message="Could not load observations" onRetry={() => refetch()} />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Notifications</CardTitle>
        {!isLoading && data && (
          <span className="text-sm text-muted">{data.length} notification{data.length !== 1 ? 's' : ''}</span>
        )}
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-3 animate-pulse">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="h-16 bg-surface-hover rounded" />
            ))}
          </div>
        ) : data && data.length > 0 ? (
          <div className="space-y-3 max-h-80 overflow-y-auto">
            {data.map((obs) => (
              <div
                key={obs.id}
                className="p-3 rounded-md bg-surface-hover border border-border"
              >
                <div className="flex items-start justify-between gap-2">
                  <p className="text-sm font-medium text-text">{obs.title}</p>
                  <Badge variant={typeVariants[obs.type] ?? 'info'}>{obs.type}</Badge>
                </div>
                <p className="text-xs text-muted mt-1">{obs.message}</p>
                <p className="text-xs text-muted mt-1">
                  {new Date(obs.date).toLocaleDateString()}
                </p>
              </div>
            ))}
          </div>
        ) : (
          <EmptyState
            title="No notifications"
            description="You're all caught up"
          />
        )}
      </CardContent>
    </Card>
  );
}
