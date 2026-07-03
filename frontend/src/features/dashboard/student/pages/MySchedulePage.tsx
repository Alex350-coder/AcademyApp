import { useMemo } from 'react';
import { Card, CardContent } from '@/shared/components/Card';
import { Badge } from '@/shared/components/Badge';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useMySchedule } from '../api/useStudentData';
import type { ScheduleEntry } from '../api/types';

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
const DAY_LABELS_ES: Record<string, string> = {
  MONDAY: 'Lunes',
  TUESDAY: 'Martes',
  WEDNESDAY: 'Miércoles',
  THURSDAY: 'Jueves',
  FRIDAY: 'Viernes',
};
const DAY_LABELS_ES_SHORT: Record<string, string> = {
  MONDAY: 'Lun',
  TUESDAY: 'Mar',
  WEDNESDAY: 'Mié',
  THURSDAY: 'Jue',
  FRIDAY: 'Vie',
};

export default function MySchedulePage() {
  const { data, isLoading, isError, refetch } = useMySchedule();

  // dayOfWeek/startTime aren't populated by the backend yet (no timetable
  // feature exists) - only entries with real schedule data are shown here.
  const scheduled = useMemo(
    () => (data ?? []).filter((entry) => entry.dayOfWeek && entry.startTime),
    [data],
  );

  const scheduleMap = useMemo(() => {
    const map: Record<string, ScheduleEntry[]> = {};
    scheduled.forEach((entry) => {
      if (!map[entry.dayOfWeek]) map[entry.dayOfWeek] = [];
      map[entry.dayOfWeek].push(entry);
    });
    return map;
  }, [scheduled]);

  if (isError) {
    return <ErrorState message="No se pudo cargar tu horario" onRetry={() => refetch()} />;
  }

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-text">Mi Horario</h1>
          <p className="text-muted text-sm mt-1">Horario semanal de clases</p>
        </div>
        <Card>
          <CardContent>
            <div className="space-y-4 animate-pulse">
              {Array.from({ length: 5 }).map((_, i) => (
                <div key={i} className="h-24 bg-surface-hover rounded" />
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (scheduled.length === 0) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-text">Mi Horario</h1>
          <p className="text-muted text-sm mt-1">Horario semanal de clases</p>
        </div>
        <EmptyState
          title="Sin horario disponible"
          description={
            !data || data.length === 0
              ? 'Tu horario está vacío. Matricúlate en cursos para ver tu horario aquí.'
              : 'Aún no se han configurado los horarios de tus cursos.'
          }
        />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Mi Horario</h1>
        <p className="text-muted text-sm mt-1">Horario semanal de clases</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
        {DAYS.map((day) => {
          const dayClasses = scheduleMap[day] ?? [];
          dayClasses.sort((a, b) => a.startTime.localeCompare(b.startTime));

          return (
            <Card key={day} className="min-h-[200px]">
              <CardContent>
                <h3 className="text-sm font-semibold text-text mb-3 text-center uppercase tracking-wider">
                  {DAY_LABELS_ES_SHORT[day] ?? day}
                </h3>
                {dayClasses.length > 0 ? (
                  <div className="space-y-2">
                    {dayClasses.map((entry) => (
                      <div
                        key={entry.id}
                        className="p-2 rounded-md bg-primary/5 border border-primary/20"
                      >
                        <p className="text-xs font-medium text-text leading-tight">{entry.courseName}</p>
                        <p className="text-[10px] text-muted mt-0.5">
                          {entry.startTime} - {entry.endTime}
                        </p>
                        <p className="text-[10px] text-muted">{entry.classroom}</p>
                        <Badge variant="default" className="mt-1 text-[10px] px-1.5 py-0">
                          {entry.courseCode}
                        </Badge>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-xs text-muted text-center mt-8">Sin clases</p>
                )}
              </CardContent>
            </Card>
          );
        })}
      </div>

      <Card>
        <CardContent>
          <h3 className="text-sm font-semibold text-text mb-2">Horario Detallado</h3>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border">
                  <th className="px-3 py-2 text-left text-muted font-medium">Día</th>
                  <th className="px-3 py-2 text-left text-muted font-medium">Curso</th>
                  <th className="px-3 py-2 text-left text-muted font-medium">Hora</th>
                  <th className="px-3 py-2 text-left text-muted font-medium">Aula</th>
                  <th className="px-3 py-2 text-left text-muted font-medium">Docente</th>
                </tr>
              </thead>
              <tbody>
                {[...scheduled]
                  .sort((a, b) => {
                    const dayDiff = DAYS.indexOf(a.dayOfWeek) - DAYS.indexOf(b.dayOfWeek);
                    if (dayDiff !== 0) return dayDiff;
                    return a.startTime.localeCompare(b.startTime);
                  })
                  .map((entry) => (
                    <tr
                      key={entry.id}
                      className="border-b border-border last:border-b-0 hover:bg-surface-hover transition-colors"
                    >
                      <td className="px-3 py-2 text-text">
                        {DAY_LABELS_ES[entry.dayOfWeek] ?? entry.dayOfWeek}
                      </td>
                      <td className="px-3 py-2">
                        <span className="font-medium text-text">{entry.courseName}</span>
                        <span className="text-muted ml-1">({entry.courseCode})</span>
                      </td>
                      <td className="px-3 py-2 text-muted">
                        {entry.startTime} - {entry.endTime}
                      </td>
                      <td className="px-3 py-2 text-muted">{entry.classroom}</td>
                      <td className="px-3 py-2 text-muted">{entry.teacherName}</td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
