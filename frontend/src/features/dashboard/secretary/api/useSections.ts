import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { secretaryEndpoints } from './endpoints';
import type { Section } from './types';

export function useSections() {
  return useQuery<Section[]>({
    queryKey: ['sections'],
    queryFn: async () => {
      const { data } = await httpClient.get(secretaryEndpoints.sections);
      return data;
    },
  });
}

export function useAvailableSections() {
  return useQuery<Section[]>({
    queryKey: ['sections', 'available'],
    queryFn: async () => {
      const { data } = await httpClient.get(secretaryEndpoints.availableSections);
      return data;
    },
  });
}
