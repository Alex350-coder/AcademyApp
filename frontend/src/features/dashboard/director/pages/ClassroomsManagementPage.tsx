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
      addToast('Classroom deleted successfully', 'success');
    },
    onError: () => {
      addToast('Failed to delete classroom', 'error');
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
    { key: 'code', header: 'Code', sortable: true },
    { key: 'name', header: 'Name', sortable: true },
    { key: 'capacity', header: 'Capacity', sortable: true },
    { key: 'location', header: 'Location', sortable: true },
    {
      key: 'actions',
      header: 'Actions',
      render: (c) => (
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => { setEditingClassroom(c); setShowForm(true); }}
          >
            Edit
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              if (window.confirm(`Delete classroom "${c.name}"?`)) {
                deleteMutation.mutate(c.id);
              }
            }}
          >
            Delete
          </Button>
        </div>
      ),
    },
  ];

  if (isError) {
    return <ErrorState message="Could not load classrooms" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text">Classrooms Management</h1>
          <p className="text-muted text-sm mt-1">Manage classrooms and their availability</p>
        </div>
        <Button onClick={() => { setEditingClassroom(null); setShowForm(true); }}>
          Add Classroom
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>All Classrooms</CardTitle>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Search classrooms..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-64"
            />
            <span className="text-sm text-muted">{filtered.length} classroom{filtered.length !== 1 ? 's' : ''}</span>
          </div>
        </CardHeader>
        <CardContent>
          {!isLoading && filtered.length === 0 ? (
            <EmptyState
              title={search ? 'No classrooms match your search' : 'No classrooms found'}
              description={search ? 'Try a different search term' : 'Add your first classroom to get started'}
              action={search ? undefined : { label: 'Add Classroom', onClick: () => { setEditingClassroom(null); setShowForm(true); } }}
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
        addToast('Classroom updated successfully', 'success');
      } else {
        await httpClient.post(directorEndpoints.classrooms, values);
        addToast('Classroom created successfully', 'success');
      }
      onSaved();
    } catch {
      addToast('Failed to save classroom', 'error');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>{classroom ? 'Edit Classroom' : 'Add Classroom'}</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Classroom Code"
              error={errors.code?.message}
              {...register('code', { required: 'Code is required' })}
            />
            <Input
              label="Classroom Name"
              error={errors.name?.message}
              {...register('name', { required: 'Name is required' })}
            />
            <Input
              label="Capacity"
              type="number"
              error={errors.capacity?.message}
              {...register('capacity', {
                required: 'Capacity is required',
                min: { value: 1, message: 'Minimum capacity is 1' },
                valueAsNumber: true,
              })}
            />
            <Input
              label="Location"
              error={errors.location?.message}
              {...register('location', { required: 'Location is required' })}
            />
            <div className="flex justify-end gap-3 pt-2">
              <Button variant="secondary" type="button" onClick={onClose}>
                Cancel
              </Button>
              <Button type="submit" loading={isSubmitting}>
                Save
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
