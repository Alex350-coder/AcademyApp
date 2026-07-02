export const API_BASE = '/api/v1';

export const endpoints = {
  auth: {
    login: `${API_BASE}/auth/login`,
    register: `${API_BASE}/auth/register`,
    registerInstitution: `${API_BASE}/auth/register-institution`,
    refresh: `${API_BASE}/auth/refresh`,
    logout: `${API_BASE}/auth/logout`,
    forgotPassword: `${API_BASE}/auth/forgot-password`,
    resetPassword: `${API_BASE}/auth/reset-password`,
  },
  institutions: {
    list: `${API_BASE}/institutions`,
  },
  users: {
    base: `${API_BASE}/users`,
    byId: (id: string) => `${API_BASE}/users/${id}`,
  },
  courses: {
    base: `${API_BASE}/courses`,
    byId: (id: string) => `${API_BASE}/courses/${id}`,
  },
  sections: {
    base: `${API_BASE}/sections`,
    byId: (id: string) => `${API_BASE}/sections/${id}`,
  },
  enrollments: {
    base: `${API_BASE}/enrollments`,
  },
  grades: {
    base: `${API_BASE}/grades`,
    byStudent: (id: string) => `${API_BASE}/grades/student/${id}`,
  },
  attendance: {
    base: `${API_BASE}/attendance`,
  },
} as const;
