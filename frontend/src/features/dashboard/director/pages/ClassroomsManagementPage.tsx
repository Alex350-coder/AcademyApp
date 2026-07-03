import { useMemo, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import httpClient from '@/shared/api/httpClient';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { Input } from '@/shared/components/Input';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useToastStore } from '@/shared/store/useToastStore';
import { DataTable, type Column } from '@/shared/components/data-display/DataTable';
import { directorEndpoints } from '../api/endpoints';
import type { Classroom } from '../api/types';

interface ClassroomFormValues {
  code: string;
  name: string;
  capacity: number;
  location: string;
}

export default function ClassroomsManagementPage() {
  const queryClient = useQueryClient();
  const addToast = useToastStore((s) => s.addToast);
  const [search, setSearch] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingClassroom, setEditingClassroom] = useState<Classroom | null>(null);

  const { data, isLoading, isError, refetch } = useQuery<Classroom[]>({
    queryKey: ['classrooms'],
    queryFn: async () => {
      const { data } = await httpClient.get(directorEndpoints.classrooms);
      return data;
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => {
      await httpClient.delete(directorEndpoints.classroomById(id));
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['classrooms'] });
      addToast('Aula eliminada correctamente', 'success');
    },
    onError: () => {
      addToast('Error al eliminar el aula', 'error');
    },
  });

  const filtered = useMemo(() => {
    if (!data) return [];
    if (!search) return data;
    const q = search.toLowerCase();
    return data.filter(
      (c) =>
        c.name.toLowerCase().includes(q) ||
        c.code.toLowerCase().includes(q) ||
        c.location.toLowerCase().includes(q),
    );
  }, [data, search]);

  const columns: Column<Classroom>[] = [
    { key: 'code', header: 'Código', sortable: true },
    { key: 'name', header: 'Nombre', sortable: true },
    { key: 'capacity', header: 'Capacidad', sortable: true },
    { key: 'location', header: 'Ubicación', sortable: true },
    {
      key: 'actions',
      header: 'Acciones',
      render: (c) => (
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => { setEditingClassroom(c); setShowForm(true); }}
          >
            Editar
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              if (window.confirm(`¿Eliminar el aula "${c.name}"?`)) {
                deleteMutation.mutate(c.id);
              }
            }}
          >
            Eliminar
          </Button>
        </div>
      ),
    },
  ];

  if (isError) {
    return <ErrorState message="No se pudieron cargar las aulas" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text">Gestión de Aulas</h1>
          <p className="text-muted text-sm mt-1">Gestiona las aulas y su disponibilidad</p>
        </div>
        <Button onClick={() => { setEditingClassroom(null); setShowForm(true); }}>
          Agregar Aula
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Todas las Aulas</CardTitle>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Buscar aulas..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-64"
            />
            <span className="text-sm text-muted">{filtered.length} aula{filtered.length !== 1 ? 's' : ''}</span>
          </div>
        </CardHeader>
        <CardContent>
          {!isLoading && filtered.length === 0 ? (
            <EmptyState
              title={search ? 'Ningún aula coincide con tu búsqueda' : 'No se encontraron aulas'}
              description={search ? 'Prueba con otro término de búsqueda' : 'Agrega tu primera aula para empezar'}
              action={search ? undefined : { label: 'Agregar Aula', onClick: () => { setEditingClassroom(null); setShowForm(true); } }}
            />
          ) : (
            <DataTable<Classroom>
              columns={columns}
              data={filtered}
              loading={isLoading}
              keyExtractor={(c) => c.id}
            />
          )}
        </CardContent>
      </Card>

      {showForm && (
        <ClassroomFormDialog
          classroom={editingClassroom}
          onClose={() => { setShowForm(false); setEditingClassroom(null); }}
          onSaved={() => {
            queryClient.invalidateQueries({ queryKey: ['classrooms'] });
            setShowForm(false);
            setEditingClassroom(null);
          }}
        />
      )}
    </div>
  );
}

function ClassroomFormDialog({
  classroom,
  onClose,
  onSaved,
}: {
  classroom: Classroom | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const addToast = useToastStore((s) => s.addToast);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ClassroomFormValues>({
    defaultValues: classroom
      ? { code: classroom.code, name: classroom.name, capacity: classroom.capacity, location: classroom.location }
      : { code: '', name: '', capacity: 30, location: '' },
  });

  const onSubmit = async (values: ClassroomFormValues) => {
    try {
      if (classroom) {
        await httpClient.put(directorEndpoints.classroomById(classroom.id), values);
        addToast('Aula actualizada correctamente', 'success');
      } else {
        await httpClient.post(directorEndpoints.classrooms, values);
        addToast('Aula creada correctamente', 'success');
      }
      onSaved();
    } catch {
      addToast('Error al guardar el aula', 'error');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>{classroom ? 'Editar Aula' : 'Agregar Aula'}</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Código del Aula"
              error={errors.code?.message}
              {...register('code', { required: 'El código es requerido' })}
            />
            <Input
              label="Nombre del Aula"
              error={errors.name?.message}
              {...register('name', { required: 'El nombre es requerido' })}
            />
            <Input
              label="Capacidad"
              type="number"
              error={errors.capacity?.message}
              {...register('capacity', {
                required: 'La capacidad es requerida',
                min: { value: 1, message: 'La capacidad mínima es 1' },
                valueAsNumber: true,
              })}
            />
            <Input
              label="Ubicación"
              error={errors.location?.message}
              {...register('location', { required: 'La ubicación es requerida' })}
            />
            <div className="flex justify-end gap-3 pt-2">
              <Button variant="secondary" type="button" onClick={onClose}>
                Cancelar
              </Button>
              <Button type="submit" loading={isSubmitting}>
                Guardar
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
