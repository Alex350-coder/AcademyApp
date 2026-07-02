import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { teacherEndpoints } from './endpoints';
import type { Section } from './types';

export function useMySections() {
  return useQuery<Section[]>({
    queryKey: ['teacher', 'sections'],
    queryFn: async () => {
      const { data } = await httpClient.get(teacherEndpoints.mySections);
      return data;
    },
  });
}
