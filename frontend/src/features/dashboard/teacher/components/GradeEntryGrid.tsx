import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useEnrollmentsBySection } from '../api/useEnrollments';
import { useRecordGrade } from '../api/useGrades';
import type { Evaluation } from '../api/types';

interface GradeEntryGridProps {
  sectionId: string;
  evaluation: Evaluation;
}

export function GradeEntryGrid({ sectionId, evaluation }: GradeEntryGridProps) {
  const { data: enrollments, isLoading, isError, refetch } = useEnrollmentsBySection(sectionId);
  const recordGrade = useRecordGrade();
  const [scores, setScores] = useState<Record<string, string>>({});
  const [savedStudentIds, setSavedStudentIds] = useState<Set<string>>(new Set());

  const roster = (enrollments ?? []).filter((e) => e.status === 'ACTIVE');

  function handleSave(studentId: string) {
    const raw = scores[studentId];
    const scoreValue = Number(raw);
    if (!raw || Number.isNaN(scoreValue) || scoreValue < 0) {
      return;
    }
    recordGrade.mutate(
      { evaluationId: evaluation.id, studentId, scoreValue },
      {
        onSuccess: () => {
          setSavedStudentIds((prev) => new Set(prev).add(studentId));
        },
      },
    );
  }

  if (isError) {
    return <ErrorState message="No se pudo cargar la lista de la sección" onRetry={() => refetch()} />;
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
        title="No hay alumnos matriculados"
        description="Esta sección no tiene alumnos matriculados"
      />
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>
          {evaluation.name} ({evaluation.evaluationTypeName}) - máx {evaluation.maxScore}
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border">
                <th className="px-4 py-2 text-left text-muted font-medium">Alumno</th>
                <th className="px-4 py-2 text-left text-muted font-medium w-32">Puntaje</th>
                <th className="px-4 py-2 text-left text-muted font-medium w-24"></th>
              </tr>
            </thead>
            <tbody>
              {roster.map((enrollment) => (
                <tr
                  key={enrollment.studentId}
                  className="border-b border-border hover:bg-surface-hover transition-colors"
                >
                  <td className="px-4 py-3 text-text">{enrollment.studentName}</td>
                  <td className="px-4 py-3">
                    <input
                      type="number"
                      min={0}
                      max={evaluation.maxScore}
                      step="0.01"
                      value={scores[enrollment.studentId] ?? ''}
                      onChange={(e) =>
                        setScores((prev) => ({ ...prev, [enrollment.studentId]: e.target.value }))
                      }
                      className="w-24 px-2 py-1 rounded-md bg-surface text-text border border-border focus:outline-none focus:ring-2 focus:ring-[var(--color-border-focus)]"
                    />
                  </td>
                  <td className="px-4 py-3">
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => handleSave(enrollment.studentId)}
                      disabled={!scores[enrollment.studentId]}
                    >
                      {savedStudentIds.has(enrollment.studentId) ? 'Guardado ✓' : 'Guardar'}
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  );
}
