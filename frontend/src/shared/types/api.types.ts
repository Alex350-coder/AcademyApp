export interface ApiError {
  errorCode: string;
  message: string;
  status: number;
  timestamp: string;
  details?: string[];
}

export interface LoginRequest {
  email: string;
  password: string;
  institutionCode?: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userId: string;
  email: string;
  fullName: string;
  roles: string[];
  institutionId: string;
  institutionName: string;
  institutionCode: string;
}

export interface RegisterInstitutionRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  institutionName: string;
  institutionCode: string;
  institutionAddress?: string;
  institutionPhone?: string;
}

export interface InstitutionDto {
  id: string;
  name: string;
  code: string;
  address?: string;
  phone?: string;
  email?: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  roleName: string;
}

export interface UserDto {
  userId: string;
  email: string;
  fullName: string;
  roles: string[];
}

export type Role = 'DIRECTOR' | 'SECRETARY' | 'TEACHER' | 'STUDENT';
