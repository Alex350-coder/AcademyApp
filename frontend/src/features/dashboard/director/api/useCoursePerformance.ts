import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { directorEndpoints } from './endpoints';
import type { CoursePerformanceData } from './types';

export function useCoursePerformance() {
  return useQuery<CoursePerformanceData[]>({
    queryKey: ['course-performance'],
    queryFn: async () => {
      const { data } = await httpClient.get(directorEndpoints.coursePerformance);
      return data;
    },
    staleTime: 2 * 60 * 1000,
  });
}
