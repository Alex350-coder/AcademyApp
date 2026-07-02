import { useMemo, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/Card';
import { Button } from '@/shared/components/Button';
import { Badge } from '@/shared/components/Badge';
import { Input } from '@/shared/components/Input';
import { ErrorState } from '@/shared/components/feedback/ErrorState';
import { EmptyState } from '@/shared/components/feedback/EmptyState';
import { useToastStore } from '@/shared/components/feedback/Toast';
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
      addToast('Teacher deactivated successfully', 'success');
    },
    onError: () => {
      addToast('Failed to deactivate teacher', 'error');
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
    { key: 'fullName', header: 'Name', sortable: true },
    { key: 'email', header: 'Email', sortable: true },
    { key: 'specialty', header: 'Specialty', sortable: true },
    {
      key: 'status',
      header: 'Status',
      render: (t) => (
        <Badge variant={t.status === 'ACTIVE' ? 'success' : 'danger'}>{t.status}</Badge>
      ),
    },
  ];

  if (isError) {
    return <ErrorState message="Could not load teachers" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text">Teachers Management</h1>
          <p className="text-muted text-sm mt-1">Manage all teachers in the institution</p>
        </div>
        <Button onClick={() => { setEditingTeacher(null); setShowForm(true); }}>
          Add Teacher
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>All Teachers</CardTitle>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Search teachers..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-64"
            />
            <span className="text-sm text-muted">{filtered.length} teacher{filtered.length !== 1 ? 's' : ''}</span>
          </div>
        </CardHeader>
        <CardContent>
          {!isLoading && filtered.length === 0 ? (
            <EmptyState
              title={search ? 'No teachers match your search' : 'No teachers found'}
              description={search ? 'Try a different search term' : 'Add your first teacher to get started'}
              action={search ? undefined : { label: 'Add Teacher', onClick: () => { setEditingTeacher(null); setShowForm(true); } }}
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
                  if (window.confirm('Are you sure you want to deactivate this teacher?')) {
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
          title={editingTeacher ? 'Edit Teacher' : 'Add Teacher'}
          initialValues={editingTeacher ? { fullName: editingTeacher.fullName, email: editingTeacher.email, specialty: editingTeacher.specialty } : undefined}
          fields={['fullName', 'email', 'specialty']}
          onClose={() => { setShowForm(false); setEditingTeacher(null); }}
          onSubmit={async (values) => {
            try {
              if (editingTeacher) {
                await httpClient.put(directorEndpoints.teacherById(editingTeacher.id), values);
                addToast('Teacher updated successfully', 'success');
              } else {
                await httpClient.post(directorEndpoints.teachers, values);
                addToast('Teacher created successfully', 'success');
              }
              queryClient.invalidateQueries({ queryKey: ['teachers'] });
              setShowForm(false);
              setEditingTeacher(null);
            } catch {
              addToast('Failed to save teacher', 'error');
            }
          }}
        />
      )}
    </div>
  );
}
