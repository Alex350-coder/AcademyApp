import { useMemo, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { Badge } from '@/shared/components/Badge';
import { Input } from '@/shared/components/Input';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useToastStore } from '@/shared/store/useToastStore';
import { ManagementDataTable } from '../components/ManagementDataTable';
import { UserFormDialog } from '../components/UserFormDialog';
import { directorEndpoints } from '../api/endpoints';
import type { Student } from '../api/types';
import type { Column } from '@/shared/components/data-display/DataTable';

export default function StudentsManagementPage() {
  const queryClient = useQueryClient();
  const addToast = useToastStore((s) => s.addToast);
  const [search, setSearch] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingStudent, setEditingStudent] = useState<Student | null>(null);

  const { data, isLoading, isError, refetch } = useQuery<Student[]>({
    queryKey: ['students'],
    queryFn: async () => {
      const { data } = await httpClient.get(directorEndpoints.students);
      return data;
    },
  });

  const deactivateMutation = useMutation({
    mutationFn: async (id: string) => {
      await httpClient.patch(directorEndpoints.studentById(id), { status: 'INACTIVE' });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      addToast('Alumno desactivado correctamente', 'success');
    },
    onError: () => {
      addToast('Error al desactivar el alumno', 'error');
    },
  });

  const filtered = useMemo(() => {
    if (!data) return [];
    if (!search) return data;
    const q = search.toLowerCase();
    return data.filter(
      (s) =>
        s.fullName.toLowerCase().includes(q) ||
        s.email.toLowerCase().includes(q) ||
        s.enrollmentCode.toLowerCase().includes(q),
    );
  }, [data, search]);

  const columns: Column<Student>[] = [
    { key: 'enrollmentCode', header: 'Código de Matrícula', sortable: true },
    { key: 'fullName', header: 'Nombre', sortable: true },
    { key: 'email', header: 'Email', sortable: true },
    { key: 'guardian', header: 'Apoderado', sortable: true },
    {
      key: 'status',
      header: 'Estado',
      render: (s) => (
        <Badge variant={s.status === 'ACTIVE' ? 'success' : 'danger'}>{s.status}</Badge>
      ),
    },
  ];

  if (isError) {
    return <ErrorState message="No se pudieron cargar los alumnos" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text">Gestión de Alumnos</h1>
          <p className="text-muted text-sm mt-1">Gestiona todos los alumnos de la institución</p>
        </div>
        <Button onClick={() => { setEditingStudent(null); setShowForm(true); }}>
          Agregar Alumno
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Todos los Alumnos</CardTitle>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Buscar alumnos..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-64"
            />
            <span className="text-sm text-muted">{filtered.length} alumno{filtered.length !== 1 ? 's' : ''}</span>
          </div>
        </CardHeader>
        <CardContent>
          {!isLoading && filtered.length === 0 ? (
            <EmptyState
              title={search ? 'Ningún alumno coincide con tu búsqueda' : 'No se encontraron alumnos'}
              description={search ? 'Prueba con otro término de búsqueda' : 'Matricula tu primer alumno para empezar'}
              action={search ? undefined : { label: 'Agregar Alumno', onClick: () => { setEditingStudent(null); setShowForm(true); } }}
            />
          ) : (
            <ManagementDataTable<Student>
              columns={columns}
              data={filtered}
              loading={isLoading}
              keyExtractor={(s) => s.id}
              actions={{
                onEdit: (id) => {
                  const student = data?.find((s) => s.id === id);
                  if (student) { setEditingStudent(student); setShowForm(true); }
                },
                onDeactivate: (id) => {
                  if (window.confirm('¿Estás seguro de que deseas desactivar a este alumno?')) {
                    deactivateMutation.mutate(id);
                  }
                },
              }}
            />
          )}
        </CardContent>
      </Card>

      {showForm && (
        <UserFormDialog
          title={editingStudent ? 'Editar Alumno' : 'Agregar Alumno'}
          initialValues={editingStudent ? { fullName: editingStudent.fullName, email: editingStudent.email, guardian: editingStudent.guardian } : undefined}
          fields={['fullName', 'email', 'guardian']}
          onClose={() => { setShowForm(false); setEditingStudent(null); }}
          onSubmit={async (values) => {
            try {
              if (editingStudent) {
                await httpClient.put(directorEndpoints.studentById(editingStudent.id), values);
                addToast('Alumno actualizado correctamente', 'success');
              } else {
                await httpClient.post(directorEndpoints.students, values);
                addToast('Alumno creado correctamente', 'success');
              }
              queryClient.invalidateQueries({ queryKey: ['students'] });
              setShowForm(false);
              setEditingStudent(null);
            } catch {
              addToast('Error al guardar el alumno', 'error');
            }
          }}
        />
      )}
    </div>
  );
}
