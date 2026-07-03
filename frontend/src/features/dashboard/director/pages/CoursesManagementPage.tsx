import { useMemo, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import httpClient from '@/shared/api/httpClient';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { Badge } from '@/shared/components/Badge';
import { Input } from '@/shared/components/Input';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useToastStore } from '@/shared/store/useToastStore';
import { DataTable, type Column } from '@/shared/components/data-display/DataTable';
import { directorEndpoints } from '../api/endpoints';
import type { Course } from '../api/types';

interface CourseFormValues {
  name: string;
  code: string;
  description: string;
  credits: number;
}

export default function CoursesManagementPage() {
  const queryClient = useQueryClient();
  const addToast = useToastStore((s) => s.addToast);
  const [search, setSearch] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingCourse, setEditingCourse] = useState<Course | null>(null);

  const { data, isLoading, isError, refetch } = useQuery<Course[]>({
    queryKey: ['courses'],
    queryFn: async () => {
      const { data } = await httpClient.get(directorEndpoints.courses);
      return data;
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => {
      await httpClient.delete(directorEndpoints.courseById(id));
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['courses'] });
      addToast('Curso eliminado correctamente', 'success');
    },
    onError: () => {
      addToast('Error al eliminar el curso', 'error');
    },
  });

  const filtered = useMemo(() => {
    if (!data) return [];
    if (!search) return data;
    const q = search.toLowerCase();
    return data.filter(
      (c) =>
        c.name.toLowerCase().includes(q) ||
        c.code.toLowerCase().includes(q),
    );
  }, [data, search]);

  const columns: Column<Course>[] = [
    { key: 'code', header: 'Código', sortable: true },
    { key: 'name', header: 'Nombre', sortable: true },
    { key: 'credits', header: 'Créditos', sortable: true },
    { key: 'sectionsCount', header: 'Secciones', sortable: true },
    {
      key: 'status',
      header: 'Estado',
      render: (c) => (
        <Badge variant={c.status === 'ACTIVE' ? 'success' : 'danger'}>{c.status}</Badge>
      ),
    },
    {
      key: 'actions',
      header: 'Acciones',
      render: (c) => (
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => { setEditingCourse(c); setShowForm(true); }}
          >
            Editar
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              if (window.confirm(`¿Eliminar el curso "${c.name}"?`)) {
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
    return <ErrorState message="No se pudieron cargar los cursos" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text">Gestión de Cursos</h1>
          <p className="text-muted text-sm mt-1">Gestiona los cursos académicos y sus secciones</p>
        </div>
        <Button onClick={() => { setEditingCourse(null); setShowForm(true); }}>
          Agregar Curso
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Todos los Cursos</CardTitle>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Buscar cursos..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-64"
            />
            <span className="text-sm text-muted">{filtered.length} curso{filtered.length !== 1 ? 's' : ''}</span>
          </div>
        </CardHeader>
        <CardContent>
          {!isLoading && filtered.length === 0 ? (
            <EmptyState
              title={search ? 'Ningún curso coincide con tu búsqueda' : 'No se encontraron cursos'}
              description={search ? 'Prueba con otro término de búsqueda' : 'Crea tu primer curso para empezar'}
              action={search ? undefined : { label: 'Agregar Curso', onClick: () => { setEditingCourse(null); setShowForm(true); } }}
            />
          ) : (
            <DataTable<Course>
              columns={columns}
              data={filtered}
              loading={isLoading}
              keyExtractor={(c) => c.id}
            />
          )}
        </CardContent>
      </Card>

      {showForm && (
        <CourseFormDialog
          course={editingCourse}
          onClose={() => { setShowForm(false); setEditingCourse(null); }}
          onSaved={() => {
            queryClient.invalidateQueries({ queryKey: ['courses'] });
            setShowForm(false);
            setEditingCourse(null);
          }}
        />
      )}
    </div>
  );
}

function CourseFormDialog({
  course,
  onClose,
  onSaved,
}: {
  course: Course | null;
  onClose: () => void;
  onSaved: () => void;
}) {
  const addToast = useToastStore((s) => s.addToast);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<CourseFormValues>({
    defaultValues: course
      ? { name: course.name, code: course.code, description: course.description, credits: course.credits }
      : { name: '', code: '', description: '', credits: 3 },
  });

  const onSubmit = async (values: CourseFormValues) => {
    try {
      if (course) {
        await httpClient.put(directorEndpoints.courseById(course.id), values);
        addToast('Curso actualizado correctamente', 'success');
      } else {
        await httpClient.post(directorEndpoints.courses, values);
        addToast('Curso creado correctamente', 'success');
      }
      onSaved();
    } catch {
      addToast('Error al guardar el curso', 'error');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>{course ? 'Editar Curso' : 'Agregar Curso'}</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Nombre del Curso"
              error={errors.name?.message}
              {...register('name', { required: 'El nombre del curso es requerido' })}
            />
            <Input
              label="Código del Curso"
              error={errors.code?.message}
              {...register('code', { required: 'El código del curso es requerido' })}
            />
            <Input
              label="Descripción"
              error={errors.description?.message}
              {...register('description', { required: 'La descripción es requerida' })}
            />
            <Input
              label="Créditos"
              type="number"
              error={errors.credits?.message}
              {...register('credits', {
                required: 'Los créditos son requeridos',
                min: { value: 1, message: 'Mínimo 1 crédito' },
                valueAsNumber: true,
              })}
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
