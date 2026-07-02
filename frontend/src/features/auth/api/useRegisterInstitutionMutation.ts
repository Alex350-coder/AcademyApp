import { useMutation } from '@tanstack/react-query';
import httpClient from '@/shared/api/httpClient';
import { endpoints } from '@/shared/api/apiEndpoints';
import type { RegisterInstitutionRequest } from '@/shared/types/api.types';

export function useRegisterInstitutionMutation() {
  return useMutation({
    mutationFn: async (data: RegisterInstitutionRequest) => {
      const response = await httpClient.post(endpoints.auth.registerInstitution, data);
      return response.data as {
        userId: string;
        email: string;
        fullName: string;
        institutionId: string;
        institutionName: string;
        institutionCode: string;
      };
    },
  });
}
