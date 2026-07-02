import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { directorEndpoints } from './endpoints';
import type { InstitutionalOverview } from './types';

export function useInstitutionalOverview() {
  return useQuery<InstitutionalOverview>({
    queryKey: ['institutional-overview'],
    queryFn: async () => {
      const { data } = await httpClient.get(directorEndpoints.overview);
      return data;
    },
    staleTime: 5 * 60 * 1000,
  });
}
