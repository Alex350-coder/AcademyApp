import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { teacherEndpoints } from './endpoints';
import type { SectionEnrollment } from './types';

export function useEnrollmentsBySection(sectionId: string) {
  return useQuery<SectionEnrollment[]>({
    queryKey: ['teacher', 'enrollments', sectionId],
    queryFn: async () => {
      const { data } = await httpClient.get(teacherEndpoints.enrollmentsBySection(sectionId));
      return data;
    },
    enabled: !!sectionId,
  });
}
