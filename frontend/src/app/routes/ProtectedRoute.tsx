import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/shared/store/useAuthStore';

function readPersistedAuth(): boolean {
  try {
    const raw = localStorage.getItem('auth-storage');
    if (!raw) return false;
    return JSON.parse(raw)?.state?.isAuthenticated === true;
  } catch {
    return false;
  }
}

export function ProtectedRoute() {
  const storeAuthed = useAuthStore((s) => s.isAuthenticated);

  const persisted = readPersistedAuth();
  const ok = storeAuthed || persisted;

  if (!ok) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
