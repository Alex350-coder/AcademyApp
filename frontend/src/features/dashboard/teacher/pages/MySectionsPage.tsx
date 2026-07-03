import { Link } from 'react-router-dom';
import { Card, CardContent } from '@/shared/components/Card';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMySections } from '../api/useSections';

export default function MySectionsPage() {
  const { data, isLoading, isError, refetch } = useMySections();

  if (isError) {
    return <ErrorState message="No se pudieron cargar tus secciones" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Mis Cursos</h1>
        <p className="text-muted text-sm mt-1">Secciones que estás enseñando actualmente</p>
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
          {data.map((section) => (
            <Card key={section.id}>
              <CardContent>
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="text-lg font-semibold text-text">
                      {section.courseName ?? 'Curso sin nombre'}
                    </h3>
                    <p className="text-sm text-muted">{section.name}</p>
                  </div>
                  <Badge variant={section.enrolledCount < section.capacity ? 'info' : 'warning'}>
                    {section.enrolledCount}/{section.capacity}
                  </Badge>
                </div>
                <div className="mt-4 flex gap-2">
                  <Link
                    to={`/app/teacher/attendance?sectionId=${section.id}`}
                    className="text-sm text-primary hover:underline"
                  >
                    Tomar Asistencia
                  </Link>
                  <span className="text-muted">·</span>
                  <Link
                    to={`/app/teacher/grades?sectionId=${section.id}`}
                    className="text-sm text-primary hover:underline"
                  >
                    Gestionar Notas
                  </Link>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <EmptyState
          title="Sin secciones asignadas"
          description="Actualmente no tienes secciones asignadas para enseñar"
        />
      )}
    </div>
  );
}
