import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { directorEndpoints } from './endpoints';
import type { AtRiskStudent } from './types';

export function useAtRiskStudents() {
  return useQuery<AtRiskStudent[]>({
    queryKey: ['at-risk-students'],
    queryFn: async () => {
      const { data } = await httpClient.get(directorEndpoints.atRiskStudents);
      return data;
    },
    staleTime: 30 * 1000,
  });
}
