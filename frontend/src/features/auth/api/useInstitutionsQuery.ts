import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { endpoints } from '@/shared/api/apiEndpoints';
import type { InstitutionDto } from '@/shared/types/api.types';

export function useInstitutionsQuery() {
  return useQuery({
    queryKey: ['institutions'],
    queryFn: async () => {
      const response = await httpClient.get<InstitutionDto[]>(endpoints.institutions.list);
      return response.data;
    },
    staleTime: 5 * 60 * 1000,
  });
}
