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
import { useToastStore } from '@/shared/components/feedback/Toast';
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
      addToast('Course deleted successfully', 'success');
    },
    onError: () => {
      addToast('Failed to delete course', 'error');
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
    { key: 'code', header: 'Code', sortable: true },
    { key: 'name', header: 'Name', sortable: true },
    { key: 'credits', header: 'Credits', sortable: true },
    { key: 'sectionsCount', header: 'Sections', sortable: true },
    {
      key: 'status',
      header: 'Status',
      render: (c) => (
        <Badge variant={c.status === 'ACTIVE' ? 'success' : 'danger'}>{c.status}</Badge>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (c) => (
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => { setEditingCourse(c); setShowForm(true); }}
          >
            Edit
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => {
              if (window.confirm(`Delete course "${c.name}"?`)) {
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
    return <ErrorState message="Could not load courses" onRetry={() => refetch()} />;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-text">Courses Management</h1>
          <p className="text-muted text-sm mt-1">Manage academic courses and their sections</p>
        </div>
        <Button onClick={() => { setEditingCourse(null); setShowForm(true); }}>
          Add Course
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>All Courses</CardTitle>
          <div className="flex items-center gap-2">
            <Input
              placeholder="Search courses..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-64"
            />
            <span className="text-sm text-muted">{filtered.length} course{filtered.length !== 1 ? 's' : ''}</span>
          </div>
        </CardHeader>
        <CardContent>
          {!isLoading && filtered.length === 0 ? (
            <EmptyState
              title={search ? 'No courses match your search' : 'No courses found'}
              description={search ? 'Try a different search term' : 'Create your first course to get started'}
              action={search ? undefined : { label: 'Add Course', onClick: () => { setEditingCourse(null); setShowForm(true); } }}
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
        addToast('Course updated successfully', 'success');
      } else {
        await httpClient.post(directorEndpoints.courses, values);
        addToast('Course created successfully', 'success');
      }
      onSaved();
    } catch {
      addToast('Failed to save course', 'error');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>{course ? 'Edit Course' : 'Add Course'}</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Course Name"
              error={errors.name?.message}
              {...register('name', { required: 'Course name is required' })}
            />
            <Input
              label="Course Code"
              error={errors.code?.message}
              {...register('code', { required: 'Course code is required' })}
            />
            <Input
              label="Description"
              error={errors.description?.message}
              {...register('description', { required: 'Description is required' })}
            />
            <Input
              label="Credits"
              type="number"
              error={errors.credits?.message}
              {...register('credits', {
                required: 'Credits is required',
                min: { value: 1, message: 'Minimum 1 credit' },
                valueAsNumber: true,
              })}
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
