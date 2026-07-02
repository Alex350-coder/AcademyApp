import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/shared/store/useAuthStore';
import type { Role } from '@/shared/types/api.types';

function readPersistedRoles(): string[] {
  try {
    const raw = localStorage.getItem('auth-storage');
    if (!raw) return [];
    return JSON.parse(raw)?.state?.user?.roles ?? [];
  } catch {
    return [];
  }
}

interface RoleGuardProps {
  allowedRoles: Role[];
}

export function RoleGuard({ allowedRoles }: RoleGuardProps) {
  const storeUser = useAuthStore((s) => s.user);

  const roles = storeUser?.roles ?? readPersistedRoles();

  if (roles.length === 0) {
    return <Navigate to="/login" replace />;
  }

  const hasAccess = roles.some((r) =>
    allowedRoles.includes(r as Role),
  );

  if (!hasAccess) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-background">
        <div className="text-center">
          <h1 className="text-2xl font-semibold text-text">Access Denied</h1>
          <p className="text-muted mt-2">
            You do not have permission to access this area.
          </p>
        </div>
      </div>
    );
  }

  return <Outlet />;
}
