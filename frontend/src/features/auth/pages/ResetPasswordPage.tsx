import { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Input } from '@/shared/components/Input';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { AuthNavbar } from '../components/AuthNavbar';
import httpClient from '@/shared/api/httpClient';
import { endpoints } from '@/shared/api/apiEndpoints';

interface FormValues {
  newPassword: string;
  confirmPassword: string;
}

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm<FormValues>();

  const onSubmit = async (data: FormValues) => {
    if (!token) {
      setError('Token de recuperación inválido');
      return;
    }
    try {
      setError(null);
      await httpClient.post(endpoints.auth.resetPassword, {
        token,
        newPassword: data.newPassword,
      });
      setSuccess(true);
    } catch (err: unknown) {
      const message =
        err && typeof err === 'object' && 'response' in err
          ? (err as { response: { data: { message?: string } } }).response?.data?.message
          : 'Ocurrió un error al restablecer tu contraseña. El enlace puede haber expirado.';
      setError(message || 'Error al restablecer la contraseña');
    }
  };

  if (!token) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center p-4">
        <AuthNavbar backTo="/login" />
        <Card padding="lg" className="w-full max-w-md text-center">
          <div className="mb-6">
            <div className="mx-auto w-16 h-16 bg-danger/10 rounded-full flex items-center justify-center mb-4">
              <svg className="w-8 h-8 text-danger" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
            <h1 className="text-2xl font-bold text-text mb-2">Enlace Inválido</h1>
            <p className="text-text-secondary text-sm mb-4">
              El enlace de recuperación no es válido o ha expirado.
            </p>
            <Link
              to="/forgot-password"
              className="text-sm font-medium text-primary hover:text-primary/80 transition-colors"
            >
              Solicitar nuevo enlace
            </Link>
          </div>
        </Card>
      </div>
    );
  }

  if (success) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center p-4">
        <AuthNavbar backTo="/login" />
        <Card padding="lg" className="w-full max-w-md text-center">
          <div className="mb-6">
            <div className="mx-auto w-16 h-16 bg-success/10 rounded-full flex items-center justify-center mb-4">
              <svg className="w-8 h-8 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h1 className="text-2xl font-bold text-text mb-2">Contraseña Actualizada</h1>
            <p className="text-text-secondary text-sm mb-4">
              Tu contraseña se ha restablecido exitosamente. Ahora puedes iniciar sesión con tu nueva contraseña.
            </p>
            <Link
              to="/login"
              className="inline-block px-6 py-3 bg-primary text-white font-medium rounded-md hover:brightness-110 transition-all"
            >
              Iniciar Sesión
            </Link>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <AuthNavbar backTo="/login" />
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 w-full max-w-sm">
        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-text">Restablecer Contraseña</h1>
          <p className="text-text-secondary text-sm mt-1">
            Ingresa tu nueva contraseña.
          </p>
        </div>

        <Input
          label="Nueva Contraseña"
          type="password"
          placeholder="Mínimo 8 caracteres"
          error={errors.newPassword?.message}
          {...register('newPassword', {
            required: 'La contraseña es requerida',
            minLength: {
              value: 8,
              message: 'La contraseña debe tener al menos 8 caracteres',
            },
          })}
        />

        <Input
          label="Confirmar Contraseña"
          type="password"
          placeholder="Repite la contraseña"
          error={errors.confirmPassword?.message}
          {...register('confirmPassword', {
            required: 'Debes confirmar la contraseña',
            validate: (value) => value === watch('newPassword') || 'Las contraseñas no coinciden',
          })}
        />

        {error && (
          <p className="text-sm text-danger" role="alert">{error}</p>
        )}

        <Button type="submit" loading={isSubmitting} className="w-full">
          Restablecer Contraseña
        </Button>

        <p className="text-center text-sm text-text-secondary">
          <Link to="/login" className="text-primary hover:text-primary/80 transition-colors">
            Volver al inicio de sesión
          </Link>
        </p>
      </form>
    </div>
  );
}
