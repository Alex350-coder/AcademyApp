import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { teacherEndpoints } from './endpoints';
import type { AttendanceRecord } from './types';
import { useToastStore } from '@/shared/store/useToastStore';

export function useAttendanceBySection(sectionId: string, date: string) {
  return useQuery<AttendanceRecord[]>({
    queryKey: ['teacher', 'attendance', sectionId, date],
    queryFn: async () => {
      const { data } = await httpClient.get(
        teacherEndpoints.attendanceBySection(sectionId, date),
      );
      return data;
    },
    enabled: !!sectionId && !!date,
  });
}

export function useBulkAttendance() {
  const queryClient = useQueryClient();
  const addToast = useToastStore((s) => s.addToast);

  return useMutation({
    mutationFn: async (payload: {
      sectionId: string;
      date: string;
      attendances: { enrollmentId: string; status: string }[];
    }) => {
      const { data } = await httpClient.post(teacherEndpoints.bulkAttendance, payload);
      return data;
    },
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({
        queryKey: ['teacher', 'attendance', variables.sectionId, variables.date],
      });
      addToast('Asistencia guardada correctamente', 'success');
    },
    onError: () => {
      addToast('Error al guardar la asistencia', 'error');
    },
  });
}
