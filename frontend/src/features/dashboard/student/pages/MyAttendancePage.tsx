import { Card, CardContent } from '@/shared/components/Card';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMyAttendance } from '../api/useStudentData';

export default function MyAttendancePage() {
  const { data, isLoading, isError, refetch } = useMyAttendance();

  if (isError) {
    return <ErrorState message="No se pudieron cargar los registros de asistencia" onRetry={() => refetch()} />;
  }

  const totalPercentage =
    data && data.length > 0
      ? data.reduce((sum, s) => sum + s.percentage, 0) / data.length
      : 0;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Mi Asistencia</h1>
        <p className="text-muted text-sm mt-1">Consulta tus registros de asistencia por curso</p>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {Array.from({ length: 3 }).map((_, i) => (
            <Card key={i}>
              <CardContent>
                <div className="space-y-2 animate-pulse">
                  <div className="h-5 w-40 bg-surface-hover rounded" />
                  <div className="h-4 w-full bg-surface-hover rounded" />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : data && data.length > 0 ? (
        <>
          <Card>
            <CardContent>
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium text-text">Asistencia General</p>
                <Badge
                  variant={totalPercentage >= 80 ? 'success' : totalPercentage >= 60 ? 'warning' : 'danger'}
                >
                  {totalPercentage.toFixed(1)}%
                </Badge>
              </div>
              <div className="mt-2 w-full h-3 bg-surface-hover rounded-full overflow-hidden">
                <div
                  className={`h-full rounded-full ${
                    totalPercentage >= 80 ? 'bg-success' : totalPercentage >= 60 ? 'bg-warning' : 'bg-danger'
                  }`}
                  style={{ width: `${Math.min(totalPercentage, 100)}%` }}
                />
              </div>
            </CardContent>
          </Card>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {data.map((section) => (
              <Card key={section.sectionId}>
                <CardContent>
                  <div className="flex items-start justify-between">
                    <div>
                      <h3 className="text-lg font-semibold text-text">{section.courseName}</h3>
                    </div>
                    <Badge
                      variant={
                        section.percentage >= 80 ? 'success' : section.percentage >= 60 ? 'warning' : 'danger'
                      }
                    >
                      {section.percentage.toFixed(1)}%
                    </Badge>
                  </div>
                  <p className="text-sm text-muted mt-2">
                    {section.presentCount} presentes de {section.totalCount} clases
                  </p>
                  <div className="mt-2 w-full h-2 bg-surface-hover rounded-full overflow-hidden">
                    <div
                      className={`h-full rounded-full ${
                        section.percentage >= 80
                          ? 'bg-success'
                          : section.percentage >= 60
                          ? 'bg-warning'
                          : 'bg-danger'
                      }`}
                      style={{ width: `${Math.min(section.percentage, 100)}%` }}
                    />
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </>
      ) : (
        <EmptyState
          title="Sin registros de asistencia"
          description="Tus registros de asistencia aparecerán aquí cuando comiencen las clases"
        />
      )}
    </div>
  );
}
