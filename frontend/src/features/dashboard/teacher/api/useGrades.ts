import { useMutation, useQueryClient } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { teacherEndpoints } from './endpoints';
import { useToastStore } from '@/shared/store/useToastStore';

export function useRecordGrade() {
  const queryClient = useQueryClient();
  const addToast = useToastStore((s) => s.addToast);

  return useMutation({
    mutationFn: async (payload: {
      evaluationId: string;
      studentId: string;
      scoreValue: number;
    }) => {
      const { data } = await httpClient.post(teacherEndpoints.grades, payload);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teacher', 'grades'] });
      addToast('Grade saved', 'success');
    },
    onError: () => {
      addToast('Failed to save grade', 'error');
    },
  });
}
