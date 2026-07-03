import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { secretaryEndpoints } from './endpoints';
import type { Enrollment, SectionEnrollment } from './types';
import { useToastStore } from '@/shared/store/useToastStore';

export function useEnrollments() {
  return useQuery<Enrollment[]>({
    queryKey: ['enrollments'],
    queryFn: async () => {
      const { data } = await httpClient.get(secretaryEndpoints.enrollments);
      return data;
    },
  });
}

export function useEnrollmentsBySection(sectionId: string) {
  return useQuery<SectionEnrollment[]>({
    queryKey: ['enrollments', 'section', sectionId],
    queryFn: async () => {
      const { data } = await httpClient.get(secretaryEndpoints.enrollmentsBySection(sectionId));
      return data;
    },
    enabled: !!sectionId,
  });
}

export function usePendingTasks() {
  return useQuery({
    queryKey: ['pending-tasks'],
    queryFn: async () => {
      const { data } = await httpClient.get(secretaryEndpoints.pendingTasks);
      return data;
    },
  });
}

export function useCreateEnrollment() {
  const queryClient = useQueryClient();
  const addToast = useToastStore((s) => s.addToast);

  return useMutation({
    mutationFn: async (payload: { studentId: string; sectionId: string }) => {
      const { data } = await httpClient.post(secretaryEndpoints.enrollments, payload);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['enrollments'] });
      queryClient.invalidateQueries({ queryKey: ['sections'] });
      queryClient.invalidateQueries({ queryKey: ['pending-tasks'] });
      addToast('Matrícula creada correctamente', 'success');
    },
    onError: () => {
      addToast('Error al crear la matrícula', 'error');
    },
  });
}
