import { createBrowserRouter, Navigate } from 'react-router-dom';
import type { ComponentType } from 'react';
import { ProtectedRoute } from './ProtectedRoute';
import { RoleGuard } from './RoleGuard';
import { RoleDashboardRedirect } from './RoleDashboardRedirect';

const lazyDefault = (loader: () => Promise<{ default: ComponentType }>) => ({
  lazy: () => loader().then((m) => ({ Component: m.default })),
});

const lazyNamed = <K extends string>(
  loader: () => Promise<Record<K, ComponentType>>,
  name: K,
) => ({
  lazy: () => loader().then((m) => ({ Component: m[name] })),
});

export const router = createBrowserRouter([
  {
    path: '/',
    ...lazyDefault(() => import('@/landing/LandingPage')),
  },
  {
    path: '/login',
    ...lazyDefault(() => import('@/features/auth/pages/AuthPage')),
  },
  {
    path: '/register',
    ...lazyDefault(() => import('@/features/auth/pages/AuthPage')),
  },
  {
    path: '/forgot-password',
    ...lazyDefault(() => import('@/features/auth/pages/ForgotPasswordPage')),
  },
  {
    path: '/reset-password',
    ...lazyDefault(() => import('@/features/auth/pages/ResetPasswordPage')),
  },
  {
    path: '/app',
    element: <ProtectedRoute />,
    children: [
      {
        index: true,
        element: <RoleDashboardRedirect />,
      },
      {
        path: 'dashboard',
        element: <RoleDashboardRedirect />,
      },
      {
        path: 'director',
        element: <RoleGuard allowedRoles={['DIRECTOR']} />,
        children: [
          {
            ...lazyNamed(() => import('@/features/dashboard/director/components/DirectorLayout'), 'DirectorLayout'),
            children: [
              { index: true, ...lazyDefault(() => import('@/features/dashboard/director/pages/ReportsPage')) },
              { path: 'teachers', ...lazyDefault(() => import('@/features/dashboard/director/pages/TeachersManagementPage')) },
              { path: 'students', ...lazyDefault(() => import('@/features/dashboard/director/pages/StudentsManagementPage')) },
              { path: 'courses', ...lazyDefault(() => import('@/features/dashboard/director/pages/CoursesManagementPage')) },
              { path: 'classrooms', ...lazyDefault(() => import('@/features/dashboard/director/pages/ClassroomsManagementPage')) },
              { path: 'reports', ...lazyDefault(() => import('@/features/dashboard/director/pages/ReportsPage')) },
            ],
          },
        ],
      },
      {
        path: 'secretary',
        element: <RoleGuard allowedRoles={['SECRETARY']} />,
        children: [
          {
            ...lazyNamed(() => import('@/features/dashboard/secretary/components/SecretaryLayout'), 'SecretaryLayout'),
            children: [
              { index: true, ...lazyDefault(() => import('@/features/dashboard/secretary/pages/SecretaryDashboardPage')) },
              { path: 'enrollments', ...lazyDefault(() => import('@/features/dashboard/secretary/pages/EnrollmentWizardPage')) },
              { path: 'attendance', ...lazyDefault(() => import('@/features/dashboard/secretary/pages/AttendanceRegistryPage')) },
            ],
          },
        ],
      },
      {
        path: 'teacher',
        element: <RoleGuard allowedRoles={['TEACHER']} />,
        children: [
          {
            ...lazyNamed(() => import('@/features/dashboard/teacher/components/TeacherLayout'), 'TeacherLayout'),
            children: [
              { index: true, ...lazyDefault(() => import('@/features/dashboard/teacher/pages/TeacherDashboardPage')) },
              { path: 'courses', ...lazyDefault(() => import('@/features/dashboard/teacher/pages/MySectionsPage')) },
              { path: 'grades', ...lazyDefault(() => import('@/features/dashboard/teacher/pages/GradesPage')) },
              { path: 'attendance', ...lazyDefault(() => import('@/features/dashboard/teacher/pages/AttendanceRegistryPage')) },
            ],
          },
        ],
      },
      {
        path: 'student',
        element: <RoleGuard allowedRoles={['STUDENT']} />,
        children: [
          {
            ...lazyNamed(() => import('@/features/dashboard/student/components/StudentLayout'), 'StudentLayout'),
            children: [
              { index: true, ...lazyDefault(() => import('@/features/dashboard/student/pages/StudentDashboardPage')) },
              { path: 'courses', ...lazyDefault(() => import('@/features/dashboard/student/pages/MyCoursesPage')) },
              { path: 'grades', ...lazyDefault(() => import('@/features/dashboard/student/pages/MyGradesPage')) },
              { path: 'attendance', ...lazyDefault(() => import('@/features/dashboard/student/pages/MyAttendancePage')) },
              { path: 'schedule', ...lazyDefault(() => import('@/features/dashboard/student/pages/MySchedulePage')) },
            ],
          },
        ],
      },
    ],
  },
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);
