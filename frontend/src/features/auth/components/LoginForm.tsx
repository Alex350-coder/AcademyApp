import { useForm } from 'react-hook-form';
import { Input } from '@/shared/components/Input';
import { ClickPowerUp } from '@/components/ui/click-powerup';
import { useLoginMutation } from '../api/useLoginMutation';
import { useAuthStore, getDashboardPath } from '@/shared/store/useAuthStore';
import { useNavigate } from 'react-router-dom';

interface FormValues {
  email: string;
  password: string;
}

export function LoginForm() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormValues>();
  const loginMutation = useLoginMutation();
  const navigate = useNavigate();

  const onSubmit = (data: FormValues) => {
    loginMutation.mutate(data, {
      onSuccess: () => {
        const user = useAuthStore.getState().user;
        if (user && user.roles.length > 0) {
          const role = user.roles[0];
          const redirectMap: Record<string, string> = {
            DIRECTOR: '/app/director',
            SECRETARY: '/app/secretary',
            TEACHER: '/app/teacher',
            STUDENT: '/app/student',
          };
          navigate(redirectMap[role] || getDashboardPath(user));
        } else {
          navigate(getDashboardPath(user));
        }
      },
    });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 w-full max-w-sm">
      <div className="text-center mb-6">
        <h1 className="text-2xl font-bold text-text">Academic SaaS</h1>
        <p className="text-text-secondary text-sm mt-1">Sign in to your account</p>
      </div>

      <Input
        label="Email"
        type="email"
        placeholder="you@academy.edu"
        error={errors.email?.message}
        {...register('email', {
          required: 'Email is required',
          pattern: {
            value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            message: 'Invalid email format',
          },
        })}
      />

      <Input
        label="Password"
        type="password"
        placeholder="Enter your password"
        error={errors.password?.message}
        {...register('password', {
          required: 'Password is required',
          minLength: {
            value: 8,
            message: 'Password must be at least 8 characters',
          },
        })}
      />

      {loginMutation.isError && (
        <p className="text-sm text-danger" role="alert">
          {(loginMutation.error as Error)?.message || 'Invalid credentials'}
        </p>
      )}

      <ClickPowerUp type="submit" disabled={isSubmitting} className="w-full">
        {loginMutation.isPending ? 'Signing in...' : 'Sign In'}
      </ClickPowerUp>
    </form>
  );
}
