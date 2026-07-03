import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { BulkAttendanceGrid } from '../components/BulkAttendanceGrid';
import { useSections } from '../api/useSections';
import type { Section } from '../api/types';

export default function AttendanceRegistryPage() {
  const [selectedSectionId, setSelectedSectionId] = useState('');
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const { data: sections, isLoading, isError, refetch } = useSections();

  if (isError) {
    return <ErrorState message="No se pudieron cargar las secciones" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text">Registro de Asistencia</h1>
        <p className="text-muted text-sm mt-1">Registra y gestiona la asistencia diaria por sección</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Seleccionar Sección y Fecha</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-end gap-4">
            <div className="flex-1">
              <label className="text-sm font-medium text-text block mb-1">Sección</label>
              {isLoading ? (
                <div className="h-10 bg-surface-hover rounded animate-pulse" />
              ) : sections && sections.length > 0 ? (
                <select
                  value={selectedSectionId}
                  onChange={(e) => setSelectedSectionId(e.target.value)}
                  className="w-full px-3 py-2 rounded-md bg-surface text-text border border-border focus:outline-none focus:ring-2 focus:ring-[var(--color-border-focus)]"
                >
                  <option value="">Selecciona una sección...</option>
                  {sections.map((section: Section) => (
                    <option key={section.id} value={section.id}>
                      {section.courseName} - {section.code} ({section.teacherName})
                    </option>
                  ))}
                </select>
              ) : (
                <p className="text-sm text-muted">No hay secciones disponibles</p>
              )}
            </div>
            <div>
              <label className="text-sm font-medium text-text block mb-1">Fecha</label>
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
        <BulkAttendanceGrid sectionId={selectedSectionId} date={selectedDate} />
      ) : (
        <EmptyState
          title="Selecciona una sección"
          description="Elige una sección de la lista de arriba para registrar la asistencia"
        />
      )}
    </div>
  );
}
