import { useState } from 'react';
import { Card, CardContent } from '@/shared/components/Card';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMyGrades } from '../api/useStudentData';

export default function MyGradesPage() {
  const { data, isLoading, isError, refetch } = useMyGrades();
  const [expandedSection, setExpandedSection] = useState<string | null>(null);

  if (isError) {
    return <ErrorState message="No se pudieron cargar tus notas" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Mis Notas</h1>
        <p className="text-muted text-sm mt-1">Consulta tu rendimiento académico por curso</p>
      </div>

      {isLoading ? (
        <div className="space-y-4">
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
        <div className="space-y-4">
          {data.map((section) => {
            const isExpanded = expandedSection === section.sectionId;
            return (
              <Card key={section.sectionId}>
                <CardContent>
                  <button
                    className="w-full text-left"
                    onClick={() => setExpandedSection(isExpanded ? null : section.sectionId)}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <h3 className="text-lg font-semibold text-text">{section.courseName}</h3>
                        <p className="text-sm text-muted">{section.courseCode}</p>
                      </div>
                      <div className="flex items-center gap-3">
                        <Badge
                          variant={
                            section.average >= 70 ? 'success' : section.average >= 60 ? 'warning' : 'danger'
                          }
                        >
                          Prom: {section.average.toFixed(1)}%
                        </Badge>
                        <span className="text-muted text-sm">{isExpanded ? '▲' : '▼'}</span>
                      </div>
                    </div>
                    <p className="text-sm text-muted mt-1">Docente: {section.teacherName}</p>
                  </button>

                  {isExpanded && section.evaluations.length > 0 && (
                    <div className="mt-4 border-t border-border pt-4">
                      <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                          <thead>
                            <tr className="border-b border-border">
                              <th className="px-3 py-2 text-left text-muted font-medium">Evaluación</th>
                              <th className="px-3 py-2 text-left text-muted font-medium">Tipo</th>
                              <th className="px-3 py-2 text-left text-muted font-medium">Fecha</th>
                              <th className="px-3 py-2 text-right text-muted font-medium">Puntaje</th>
                            </tr>
                          </thead>
                          <tbody>
                            {section.evaluations.map((evalItem) => (
                              <tr key={evalItem.id} className="border-b border-border last:border-b-0">
                                <td className="px-3 py-2 text-text">{evalItem.name}</td>
                                <td className="px-3 py-2 text-muted">{evalItem.type}</td>
                                <td className="px-3 py-2 text-muted">
                                  {new Date(evalItem.date).toLocaleDateString()}
                                </td>
                                <td className="px-3 py-2 text-right">
                                  <Badge
                                    variant={
                                      evalItem.score / evalItem.maxScore >= 0.7
                                        ? 'success'
                                        : evalItem.score / evalItem.maxScore >= 0.6
                                        ? 'warning'
                                        : 'danger'
                                    }
                                  >
                                    {evalItem.score}/{evalItem.maxScore}
                                  </Badge>
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  )}

                  {isExpanded && section.evaluations.length === 0 && (
                    <p className="text-sm text-muted mt-4">Aún no hay evaluaciones registradas</p>
                  )}
                </CardContent>
              </Card>
            );
          })}
        </div>
      ) : (
        <EmptyState
          title="Sin notas disponibles"
          description="Tus notas aparecerán aquí cuando se registren evaluaciones"
        />
      )}
    </div>
  );
}
