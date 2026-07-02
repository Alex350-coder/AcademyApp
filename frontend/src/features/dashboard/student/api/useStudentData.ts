import { useQuery } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { studentEndpoints } from './endpoints';
import type {
  StudentProfile,
  CourseWithGrades,
  AttendanceSummary,
  ScheduleEntry,
  Observation,
} from './types';

export function useMyProfile() {
  return useQuery<StudentProfile>({
    queryKey: ['student-profile'],
    queryFn: async () => {
      const { data } = await httpClient.get(studentEndpoints.me);
      return data;
    },
    staleTime: 5 * 60 * 1000,
  });
}

export function useMyCourses() {
  return useQuery<CourseWithGrades[]>({
    queryKey: ['student-courses'],
    queryFn: async () => {
      const { data } = await httpClient.get(studentEndpoints.myCourses);
      return data;
    },
    staleTime: 5 * 60 * 1000,
  });
}

export function useMyGrades() {
  return useQuery<CourseWithGrades[]>({
    queryKey: ['student-grades'],
    queryFn: async () => {
      const { data } = await httpClient.get(studentEndpoints.myGrades);
      return data;
    },
    staleTime: 2 * 60 * 1000,
  });
}

export function useMyAttendance() {
  return useQuery<AttendanceSummary[]>({
    queryKey: ['student-attendance'],
    queryFn: async () => {
      const { data } = await httpClient.get(studentEndpoints.myAttendance);
      return data;
    },
    staleTime: 2 * 60 * 1000,
  });
}

export function useMySchedule() {
  return useQuery<ScheduleEntry[]>({
    queryKey: ['student-schedule'],
    queryFn: async () => {
      const { data } = await httpClient.get(studentEndpoints.mySchedule);
      return data;
    },
    staleTime: 10 * 60 * 1000,
  });
}

export function useMyObservations() {
  return useQuery<Observation[]>({
    queryKey: ['student-observations'],
    queryFn: async () => {
      const { data } = await httpClient.get(studentEndpoints.myObservations);
      return data;
    },
    staleTime: 60 * 1000,
  });
}
