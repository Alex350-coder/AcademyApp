import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { motion } from 'framer-motion';
import { Input } from '@/shared/components/Input';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { AuthNavbar } from '../components/AuthNavbar';
import { GradientDots } from '@/components/ui/gradient-dots';
import httpClient from '@/shared/api/httpClient';
import { endpoints } from '@/shared/api/apiEndpoints';

interface FormValues {
  email: string;
}

export default function ForgotPasswordPage() {
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormValues>();

  const onSubmit = async (data: FormValues) => {
    try {
      setError(null);
      await httpClient.post(endpoints.auth.forgotPassword, data);
      setSubmitted(true);
    } catch {
      setError('Ocurrió un error al procesar tu solicitud. Intenta de nuevo más tarde.');
    }
  };

  if (submitted) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center overflow-hidden relative">
        <GradientDots className="pointer-events-none" backgroundColor="transparent" duration={30} blobOpacity={0.08} />
        <AuthNavbar backTo="/login" />

        <div className="relative z-10 w-full max-w-md mx-auto px-4">
          <Card padding="lg" className="overflow-hidden rounded-2xl border border-border bg-surface/60 backdrop-blur-md shadow-elevation-2 text-center">
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ type: 'spring', stiffness: 100, damping: 10 }}
              className="mx-auto w-16 h-16 bg-success/10 rounded-full flex items-center justify-center mb-4"
            >
              <svg className="w-8 h-8 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </motion.div>
            <motion.h1
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.15 }}
              className="text-2xl font-bold text-text mb-2"
            >
              Solicitud Enviada
            </motion.h1>
            <motion.p
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.25 }}
              className="text-text-secondary text-sm"
            >
              Si el email existe, recibirás un enlace de recuperación en tu bandeja de entrada.
              Revisa también tu carpeta de spam.
            </motion.p>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.4 }}
              className="mt-6"
            >
              <Link
                to="/login"
                className="inline-block text-sm font-medium text-primary hover:text-primary/80 transition-colors"
              >
                Volver al inicio de sesión
              </Link>
            </motion.div>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center overflow-hidden relative">
      <GradientDots className="pointer-events-none" backgroundColor="transparent" duration={30} blobOpacity={0.08} />
      <AuthNavbar backTo="/login" />

      <div className="relative z-10 w-full max-w-md mx-auto px-4">
        <Card padding="lg" className="overflow-hidden rounded-2xl border border-border bg-surface/60 backdrop-blur-md shadow-elevation-2">
          <div className="flex flex-col items-center text-center mb-6">
            <motion.div
              initial={{ scale: 0, rotate: -20 }}
              animate={{ scale: 1, rotate: 0 }}
              transition={{ type: 'spring', stiffness: 120, damping: 12 }}
              className="w-14 h-14 rounded-full bg-primary/10 flex items-center justify-center mb-4"
            >
              <svg className="w-7 h-7 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M21.75 9v.906a2.25 2.25 0 0 1-1.183 1.981l-6.478 3.488M2.25 9v.906a2.25 2.25 0 0 0 1.183 1.981l6.478 3.488m8.839 2.51-4.66-2.51m0 0-1.023-.55a2.25 2.25 0 0 0-2.134 0l-1.022.55m0 0-4.661 2.51m16.5 1.615a2.25 2.25 0 0 1-2.25 2.25h-15a2.25 2.25 0 0 1-2.25-2.25V8.844a2.25 2.25 0 0 1 1.183-1.981l7.5-4.039a2.25 2.25 0 0 1 2.134 0l7.5 4.039a2.25 2.25 0 0 1 1.183 1.98V19.5Z" />
              </svg>
            </motion.div>
            <motion.h1
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.15 }}
              className="text-2xl font-bold text-text"
            >
              Recuperar Contraseña
            </motion.h1>
            <motion.p
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.25 }}
              className="text-text-secondary text-sm mt-1 max-w-xs"
            >
              Ingresa tu email y te enviaremos un enlace para restablecer tu contraseña.
            </motion.p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input
              label="Email"
              type="email"
              placeholder="tu@email.com"
              error={errors.email?.message}
              {...register('email', {
                required: 'El email es requerido',
                pattern: {
                  value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                  message: 'Formato de email inválido',
                },
              })}
            />

            {error && (
              <motion.p initial={{ opacity: 0, y: -8 }} animate={{ opacity: 1, y: 0 }} className="text-sm text-danger" role="alert">
                {error}
              </motion.p>
            )}

            <Button type="submit" loading={isSubmitting} className="w-full">
              Enviar Enlace
            </Button>

            <p className="text-center text-sm text-text-secondary">
              <Link to="/login" className="text-primary hover:text-primary/80 transition-colors">
                Volver al inicio de sesión
              </Link>
            </p>
          </form>
        </Card>
      </div>
    </div>
  );
}
