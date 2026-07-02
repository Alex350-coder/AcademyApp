import { Navigate } from 'react-router-dom';
import { LoginForm } from '../components/LoginForm';
import { useAuthStore } from '@/shared/store/useAuthStore';
import { AuthNavbar } from '../components/AuthNavbar';

export default function LoginPage() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <AuthNavbar backTo="/" />
      <LoginForm />
    </div>
  );
}
