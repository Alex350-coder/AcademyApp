import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { endpoints } from './apiEndpoints';

interface FailedRequest {
  resolve: (token: string) => void;
  reject: (error: unknown) => void;
}

let accessToken: string | null = null;
let isRefreshing = false;
let failedQueue: FailedRequest[] = [];

try {
  const raw = localStorage.getItem('auth-storage');
  if (raw) {
    const parsed = JSON.parse(raw);
    const token = parsed?.state?.accessToken;
    if (token) accessToken = token;
  }
} catch {
  /* ignore parse errors */
}

export function setAccessToken(token: string | null) {
  accessToken = token;
}

function processQueue(error: unknown, token: string | null = null) {
  failedQueue.forEach(({ resolve, reject }) => {
    if (token) {
      resolve(token);
    } else {
      reject(error);
    }
  });
  failedQueue = [];
}

const httpClient = axios.create({
  baseURL: '',
  headers: { 'Content-Type': 'application/json' },
});

httpClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  let token = accessToken;
  if (!token) {
    try {
      const raw = localStorage.getItem('auth-storage');
      if (raw) {
        const parsed = JSON.parse(raw);
        token = parsed?.state?.accessToken ?? null;
        if (token) accessToken = token;
      }
    } catch { /* ignore parse errors */ }
  }
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

httpClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry) {
      const hadAuthHeader = !!originalRequest.headers?.Authorization;

      if (!hadAuthHeader) {
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise<string>((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${token}`;
          }
          return httpClient(originalRequest);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        const { data } = await axios.post(endpoints.auth.refresh, {
          refreshToken,
        });

        const newToken: string = data.accessToken;
        setAccessToken(newToken);

        processQueue(null, newToken);

        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
        }
        return httpClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        setAccessToken(null);
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('auth-storage');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  },
);

export default httpClient;
