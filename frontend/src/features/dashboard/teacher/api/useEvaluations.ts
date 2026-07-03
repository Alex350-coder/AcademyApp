import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { teacherEndpoints } from './endpoints';
import type { Evaluation, EvaluationType } from './types';
import { useToastStore } from '@/shared/store/useToastStore';

export function useEvaluationsBySection(sectionId: string) {
  return useQuery<Evaluation[]>({
    queryKey: ['teacher', 'evaluations', sectionId],
    queryFn: async () => {
      const { data } = await httpClient.get(teacherEndpoints.evaluationsBySection(sectionId));
      return data;
    },
    enabled: !!sectionId,
  });
}

export function useEvaluationTypes() {
  return useQuery<EvaluationType[]>({
    queryKey: ['teacher', 'evaluation-types'],
    queryFn: async () => {
      const { data } = await httpClient.get(teacherEndpoints.evaluationTypes);
      return data;
    },
  });
}

export function useCreateEvaluation() {
  const queryClient = useQueryClient();
  const addToast = useToastStore((s) => s.addToast);

  return useMutation({
    mutationFn: async (payload: {
      sectionId: string;
      evaluationTypeId: string;
      name: string;
      date: string | null;
      maxScore: number;
    }) => {
      const { data } = await httpClient.post(teacherEndpoints.evaluations, payload);
      return data;
    },
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['teacher', 'evaluations', variables.sectionId] });
      addToast('Evaluación creada correctamente', 'success');
    },
    onError: () => {
      addToast('Error al crear la evaluación', 'error');
    },
  });
}
