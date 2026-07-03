import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { usePendingTasks } from '../api/useEnrollments';

export function PendingTasksWidget() {
  const navigate = useNavigate();
  const { data, isLoading, isError, refetch } = usePendingTasks();

  if (isError) {
    return <ErrorState message="No se pudieron cargar las tareas pendientes" onRetry={() => refetch()} />;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Tareas Pendientes</CardTitle>
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
                <p className="text-sm font-medium text-text">Matrículas Pendientes</p>
                <p className="text-xs text-muted">Esperando confirmación</p>
              </div>
              <span className="text-2xl font-bold text-warning">{data.pendingEnrollments ?? 0}</span>
            </div>
            <div className="flex items-center justify-between p-3 rounded-md bg-surface-hover">
              <div>
                <p className="text-sm font-medium text-text">Asistencia sin Registrar</p>
                <p className="text-xs text-muted">Falta el registro de hoy</p>
              </div>
              <span className="text-2xl font-bold text-danger">{data.unregisteredAttendance ?? 0}</span>
            </div>
            <div className="flex gap-2 pt-2">
              <Button
                variant="primary"
                size="sm"
                className="flex-1"
                onClick={() => navigate('/app/secretary/enrollments')}
              >
                Ir a Matrículas
              </Button>
              <Button
                variant="secondary"
                size="sm"
                className="flex-1"
                onClick={() => navigate('/app/secretary/attendance')}
              >
                Registrar Asistencia
              </Button>
            </div>
          </div>
        ) : (
          <p className="text-sm text-muted text-center py-4">Sin tareas pendientes</p>
        )}
      </CardContent>
    </Card>
  );
}
