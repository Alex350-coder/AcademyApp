import { Navigate } from 'react-router-dom';
import { useAuthStore, getDashboardPath } from '@/shared/store/useAuthStore';

export function RoleDashboardRedirect() {
  const user = useAuthStore((s) => s.user);
  return <Navigate to={getDashboardPath(user)} replace />;
}
