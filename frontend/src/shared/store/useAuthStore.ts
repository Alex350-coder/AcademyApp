import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { setAccessToken } from '@/shared/api/httpClient';

interface User {
  userId: string;
  email: string;
  fullName: string;
  roles: string[];
}

interface PersistedState {
  user: User | null;
  isAuthenticated: boolean;
  accessToken: string | null;
  refreshToken: string | null;
}

interface AuthState extends PersistedState {
  login: (user: User, accessToken: string, refreshToken: string) => void;
  logout: () => void;
  setUser: (user: User) => void;
}

export function getDashboardPath(user: User | null): string {
  if (!user || !user.roles.length) return '/login';
  const role = user.roles[0];
  const map: Record<string, string> = {
    DIRECTOR: '/app/director',
    SECRETARY: '/app/secretary',
    TEACHER: '/app/teacher',
    STUDENT: '/app/student',
  };
  return map[role] || '/login';
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      accessToken: null,
      refreshToken: null,

      login: (user, accessToken, refreshToken) => {
        setAccessToken(accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        set({ user, isAuthenticated: true, accessToken, refreshToken });
      },

      logout: () => {
        setAccessToken(null);
        localStorage.removeItem('refreshToken');
        set({ user: null, isAuthenticated: false, accessToken: null, refreshToken: null });
      },

      setUser: (user) => {
        set({ user });
      },
    }),
    {
      name: 'auth-storage',
      onRehydrateStorage: () => (state) => {
        if (state?.accessToken) {
          setAccessToken(state.accessToken);
        }
      },
    },
  ),
);
