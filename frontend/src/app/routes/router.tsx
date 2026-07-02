import { createBrowserRouter, Navigate } from 'react-router-dom';
import { ProtectedRoute } from './ProtectedRoute';
import { RoleGuard } from './RoleGuard';
import { RoleDashboardRedirect } from './RoleDashboardRedirect';
import AuthPage from '@/features/auth/pages/AuthPage';
import ForgotPasswordPage from '@/features/auth/pages/ForgotPasswordPage';
import ResetPasswordPage from '@/features/auth/pages/ResetPasswordPage';

import { DirectorLayout } from '@/features/dashboard/director/components/DirectorLayout';
import TeachersManagementPage from '@/features/dashboard/director/pages/TeachersManagementPage';
import StudentsManagementPage from '@/features/dashboard/director/pages/StudentsManagementPage';
import CoursesManagementPage from '@/features/dashboard/director/pages/CoursesManagementPage';
import ClassroomsManagementPage from '@/features/dashboard/director/pages/ClassroomsManagementPage';
import ReportsPage from '@/features/dashboard/director/pages/ReportsPage';

import { SecretaryLayout } from '@/features/dashboard/secretary/components/SecretaryLayout';
import SecretaryDashboardPage from '@/features/dashboard/secretary/pages/SecretaryDashboardPage';
import EnrollmentWizardPage from '@/features/dashboard/secretary/pages/EnrollmentWizardPage';
import AttendanceRegistryPage from '@/features/dashboard/secretary/pages/AttendanceRegistryPage';

import { TeacherLayout } from '@/features/dashboard/teacher/components/TeacherLayout';
import TeacherDashboardPage from '@/features/dashboard/teacher/pages/TeacherDashboardPage';
import MySectionsPage from '@/features/dashboard/teacher/pages/MySectionsPage';
import TeacherAttendanceRegistryPage from '@/features/dashboard/teacher/pages/AttendanceRegistryPage';
import GradesPage from '@/features/dashboard/teacher/pages/GradesPage';

import { StudentLayout } from '@/features/dashboard/student/components/StudentLayout';
import StudentDashboardPage from '@/features/dashboard/student/pages/StudentDashboardPage';
import MyCoursesPage from '@/features/dashboard/student/pages/MyCoursesPage';
import MySchedulePage from '@/features/dashboard/student/pages/MySchedulePage';
import MyAttendancePage from '@/features/dashboard/student/pages/MyAttendancePage';
import MyGradesPage from '@/features/dashboard/student/pages/MyGradesPage';

import LandingPage from '@/landing/LandingPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <LandingPage />,
  },
  {
    path: '/login',
    element: <AuthPage />,
  },
  {
    path: '/register',
    element: <AuthPage />,
  },
  {
    path: '/forgot-password',
    element: <ForgotPasswordPage />,
  },
  {
    path: '/reset-password',
    element: <ResetPasswordPage />,
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
            element: <DirectorLayout />,
            children: [
              { index: true, element: <ReportsPage /> },
              { path: 'teachers', element: <TeachersManagementPage /> },
              { path: 'students', element: <StudentsManagementPage /> },
              { path: 'courses', element: <CoursesManagementPage /> },
              { path: 'classrooms', element: <ClassroomsManagementPage /> },
              { path: 'reports', element: <ReportsPage /> },
            ],
          },
        ],
      },
      {
        path: 'secretary',
        element: <RoleGuard allowedRoles={['SECRETARY']} />,
        children: [
          {
            element: <SecretaryLayout />,
            children: [
              { index: true, element: <SecretaryDashboardPage /> },
              { path: 'enrollments', element: <EnrollmentWizardPage /> },
              { path: 'attendance', element: <AttendanceRegistryPage /> },
            ],
          },
        ],
      },
      {
        path: 'teacher',
        element: <RoleGuard allowedRoles={['TEACHER']} />,
        children: [
          {
            element: <TeacherLayout />,
            children: [
              { index: true, element: <TeacherDashboardPage /> },
              { path: 'courses', element: <MySectionsPage /> },
              { path: 'grades', element: <GradesPage /> },
              { path: 'attendance', element: <TeacherAttendanceRegistryPage /> },
            ],
          },
        ],
      },
      {
        path: 'student',
        element: <RoleGuard allowedRoles={['STUDENT']} />,
        children: [
          {
            element: <StudentLayout />,
            children: [
              { index: true, element: <StudentDashboardPage /> },
              { path: 'courses', element: <MyCoursesPage /> },
              { path: 'grades', element: <MyGradesPage /> },
              { path: 'attendance', element: <MyAttendancePage /> },
              { path: 'schedule', element: <MySchedulePage /> },
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
