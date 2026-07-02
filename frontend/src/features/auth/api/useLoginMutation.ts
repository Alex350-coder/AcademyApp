import { useMutation } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { endpoints } from '@/shared/api/apiEndpoints';
import { useAuthStore } from '@/shared/store/useAuthStore';
import type { LoginRequest, LoginResponse } from '@/shared/types/api.types';

export function useLoginMutation() {
  const login = useAuthStore((s) => s.login);

  return useMutation({
    mutationFn: async (data: LoginRequest) => {
      const response = await httpClient.post<LoginResponse>(
        endpoints.auth.login,
        data,
      );
      return response.data;
    },
    onSuccess: (data) => {
      login(
        {
          userId: data.userId,
          email: data.email,
          fullName: data.fullName,
          roles: data.roles,
        },
        data.accessToken,
        data.refreshToken,
      );
    },
  });
}
