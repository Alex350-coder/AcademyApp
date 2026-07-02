import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { directorEndpoints } from './endpoints';
import type { AttendanceTrendData } from './types';

export function useAttendanceTrend() {
  return useQuery<AttendanceTrendData[]>({
    queryKey: ['attendance-trend'],
    queryFn: async () => {
      const { data } = await httpClient.get(directorEndpoints.attendanceTrend);
      return data;
    },
    staleTime: 2 * 60 * 1000,
  });
}
