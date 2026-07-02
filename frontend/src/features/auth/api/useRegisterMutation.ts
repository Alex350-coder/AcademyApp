import { useMutation } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { endpoints } from '@/shared/api/apiEndpoints';
import type { RegisterRequest, LoginResponse } from '@/shared/types/api.types';

export function useRegisterMutation() {
  return useMutation({
    mutationFn: async (data: RegisterRequest) => {
      const response = await httpClient.post<LoginResponse>(
        endpoints.auth.register,
        data,
      );
      return response.data;
    },
  });
}
