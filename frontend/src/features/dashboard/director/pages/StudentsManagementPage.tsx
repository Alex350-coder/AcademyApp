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
      addToast('Student deactivated successfully', 'success');
    },
    onError: () => {
      addToast('Failed to deactivate student', 'error');
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
    { key: 'enrollmentCode', header: 'Enrollment Code', sortable: true },
    { key: 'fullName', header: 'Name', sortable: true },
    { key: 'email', header: 'Email', sortable: true },
    { key: 'guardian', header: 'Guardian', sortable: true },
    {
      key: 'status',
      header: 'Status',
      render: (s) => (
        <Badge variant={s.status === 'ACTIVE' ? 'success' : 'danger'}>{s.status}</Badge>
      ),
    },
  ];

  if (isError) {
    return <ErrorState message="Could not load students" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text">Students Management</h1>
          <p className="text-muted text-sm mt-1">Manage all students in the institution</p>
        </div>
        <Button onClick={() => { setEditingStudent(null); setShowForm(true); }}>
          Add Student
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>All Students</CardTitle>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Search students..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-64"
            />
            <span className="text-sm text-muted">{filtered.length} student{filtered.length !== 1 ? 's' : ''}</span>
          </div>
        </CardHeader>
        <CardContent>
          {!isLoading && filtered.length === 0 ? (
            <EmptyState
              title={search ? 'No students match your search' : 'No students found'}
              description={search ? 'Try a different search term' : 'Enroll your first student to get started'}
              action={search ? undefined : { label: 'Add Student', onClick: () => { setEditingStudent(null); setShowForm(true); } }}
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
                  if (window.confirm('Are you sure you want to deactivate this student?')) {
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
          title={editingStudent ? 'Edit Student' : 'Add Student'}
          initialValues={editingStudent ? { fullName: editingStudent.fullName, email: editingStudent.email, guardian: editingStudent.guardian } : undefined}
          fields={['fullName', 'email', 'guardian']}
          onClose={() => { setShowForm(false); setEditingStudent(null); }}
          onSubmit={async (values) => {
            try {
              if (editingStudent) {
                await httpClient.put(directorEndpoints.studentById(editingStudent.id), values);
                addToast('Student updated successfully', 'success');
              } else {
                await httpClient.post(directorEndpoints.students, values);
                addToast('Student created successfully', 'success');
              }
              queryClient.invalidateQueries({ queryKey: ['students'] });
              setShowForm(false);
              setEditingStudent(null);
            } catch {
              addToast('Failed to save student', 'error');
            }
          }}
        />
      )}
    </div>
  );
}
