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
import type { Teacher } from '../api/types';
import type { Column } from '@/shared/components/data-display/DataTable';

export default function TeachersManagementPage() {
  const queryClient = useQueryClient();
  const addToast = useToastStore((s) => s.addToast);
  const [search, setSearch] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingTeacher, setEditingTeacher] = useState<Teacher | null>(null);

  const { data, isLoading, isError, refetch } = useQuery<Teacher[]>({
    queryKey: ['teachers'],
    queryFn: async () => {
      const { data } = await httpClient.get(directorEndpoints.teachers);
      return data;
    },
  });

  const deactivateMutation = useMutation({
    mutationFn: async (id: string) => {
      await httpClient.patch(directorEndpoints.teacherById(id), { status: 'INACTIVE' });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teachers'] });
      addToast('Docente desactivado correctamente', 'success');
    },
    onError: () => {
      addToast('Error al desactivar el docente', 'error');
    },
  });

  const filtered = useMemo(() => {
    if (!data) return [];
    if (!search) return data;
    const q = search.toLowerCase();
    return data.filter(
      (t) =>
        t.fullName.toLowerCase().includes(q) ||
        t.email.toLowerCase().includes(q) ||
        t.specialty.toLowerCase().includes(q),
    );
  }, [data, search]);

  const columns: Column<Teacher>[] = [
    { key: 'fullName', header: 'Nombre', sortable: true },
    { key: 'email', header: 'Email', sortable: true },
    { key: 'specialty', header: 'Especialidad', sortable: true },
    {
      key: 'status',
      header: 'Estado',
      render: (t) => (
        <Badge variant={t.status === 'ACTIVE' ? 'success' : 'danger'}>{t.status}</Badge>
      ),
    },
  ];

  if (isError) {
    return <ErrorState message="No se pudieron cargar los docentes" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text">Gestión de Docentes</h1>
          <p className="text-muted text-sm mt-1">Gestiona todos los docentes de la institución</p>
        </div>
        <Button onClick={() => { setEditingTeacher(null); setShowForm(true); }}>
          Agregar Docente
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Todos los Docentes</CardTitle>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Buscar docentes..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-64"
            />
            <span className="text-sm text-muted">{filtered.length} docente{filtered.length !== 1 ? 's' : ''}</span>
          </div>
        </CardHeader>
        <CardContent>
          {!isLoading && filtered.length === 0 ? (
            <EmptyState
              title={search ? 'Ningún docente coincide con tu búsqueda' : 'No se encontraron docentes'}
              description={search ? 'Prueba con otro término de búsqueda' : 'Agrega tu primer docente para empezar'}
              action={search ? undefined : { label: 'Agregar Docente', onClick: () => { setEditingTeacher(null); setShowForm(true); } }}
            />
          ) : (
            <ManagementDataTable<Teacher>
              columns={columns}
              data={filtered}
              loading={isLoading}
              keyExtractor={(t) => t.id}
              actions={{
                onEdit: (id) => {
                  const teacher = data?.find((t) => t.id === id);
                  if (teacher) { setEditingTeacher(teacher); setShowForm(true); }
                },
                onDeactivate: (id) => {
                  if (window.confirm('¿Estás seguro de que deseas desactivar a este docente?')) {
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
          title={editingTeacher ? 'Editar Docente' : 'Agregar Docente'}
          initialValues={editingTeacher ? { fullName: editingTeacher.fullName, email: editingTeacher.email, specialty: editingTeacher.specialty } : undefined}
          fields={['fullName', 'email', 'specialty']}
          onClose={() => { setShowForm(false); setEditingTeacher(null); }}
          onSubmit={async (values) => {
            try {
              if (editingTeacher) {
                await httpClient.put(directorEndpoints.teacherById(editingTeacher.id), values);
                addToast('Docente actualizado correctamente', 'success');
              } else {
                await httpClient.post(directorEndpoints.teachers, values);
                addToast('Docente creado correctamente', 'success');
              }
              queryClient.invalidateQueries({ queryKey: ['teachers'] });
              setShowForm(false);
              setEditingTeacher(null);
            } catch {
              addToast('Error al guardar el docente', 'error');
            }
          }}
        />
      )}
    </div>
  );
}
