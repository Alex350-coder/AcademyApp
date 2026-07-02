import { type FormEvent, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { AnimatedInput } from './AnimatedInput';
import { BoxReveal } from './auth-decorations';
import { ClickPowerUp } from '@/components/ui/click-powerup';
import { useLoginMutation } from '../api/useLoginMutation';
import { useRegisterInstitutionMutation } from '../api/useRegisterInstitutionMutation';
import { useInstitutionsQuery } from '../api/useInstitutionsQuery';
import { useAuthStore, getDashboardPath } from '@/shared/store/useAuthStore';

interface LoginFields {
  email: string;
  password: string;
  institutionCode?: string;
}

interface RegisterFields {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  institutionName: string;
  institutionCode: string;
  institutionAddress: string;
  institutionPhone: string;
}

type AuthMode = 'login' | 'register';

interface AnimatedFormProps {
  mode?: AuthMode;
  onToggleMode?: () => void;
}

export function AnimatedForm({ mode: controlledMode, onToggleMode }: AnimatedFormProps) {
  const [internalMode, setInternalMode] = useState<AuthMode>('login');
  const navigate = useNavigate();
  const loginMutation = useLoginMutation();
  const registerMutation = useRegisterInstitutionMutation();
  const { data: institutions, isLoading: instLoading, isError: instError } = useInstitutionsQuery();

  const isControlled = controlledMode !== undefined;
  const mode = isControlled ? controlledMode : internalMode;
  const isLogin = mode === 'login';

  const loginForm = useForm<LoginFields>();
  const registerForm = useForm<RegisterFields>();

  const [selectedInst, setSelectedInst] = useState('');

  useEffect(() => {
    if (isLogin && institutions && institutions.length > 0 && !selectedInst) {
      setSelectedInst(institutions[0].code);
    }
  }, [isLogin, institutions, selectedInst]);

  const onLoginSubmit = (data: LoginFields) => {
    loginMutation.mutate(
      { ...data, institutionCode: selectedInst || undefined },
      {
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
      },
    );
  };

  const onRegisterSubmit = (data: RegisterFields) => {
    const { confirmPassword: _, ...payload } = data;
    registerMutation.mutate(payload, {
      onSuccess: () => {
        if (isControlled) {
          onToggleMode?.();
        } else {
          setInternalMode('login');
        }
      },
    });
  };

  const handleToggleMode = (e: FormEvent) => {
    e.preventDefault();
    if (isControlled) {
      onToggleMode?.();
    } else {
      setInternalMode(isLogin ? 'register' : 'login');
    }
  };

  const loginEmail = loginForm.register('email', {
    required: 'El email es requerido',
    pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: 'Email inválido' },
  });
  const loginPassword = loginForm.register('password', {
    required: 'La contraseña es requerida',
  });

  const regFirstName = registerForm.register('firstName', { required: 'El nombre es requerido' });
  const regLastName = registerForm.register('lastName', { required: 'El apellido es requerido' });
  const regEmail = registerForm.register('email', {
    required: 'El email es requerido',
    pattern: { value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: 'Email inválido' },
  });
  const regPassword = registerForm.register('password', {
    required: 'La contraseña es requerida',
    minLength: { value: 8, message: 'Mínimo 8 caracteres' },
  });
  const regConfirmPassword = registerForm.register('confirmPassword', {
    required: 'Debes confirmar la contraseña',
    validate: (value: string) => value === registerForm.watch('password') || 'Las contraseñas no coinciden',
  });
  const regInstName = registerForm.register('institutionName', { required: 'El nombre de la IE es requerido' });
  const regInstCode = registerForm.register('institutionCode', { required: 'El código es requerido' });
  const regInstAddress = registerForm.register('institutionAddress');

  return (
    <section className="max-md:w-full flex flex-col gap-4 w-full max-w-[420px] mx-auto px-6">
      <BoxReveal boxColor="var(--color-primary)" duration={0.3}>
        <h2 className="font-bold text-3xl text-text text-center">
          {isLogin ? 'Iniciar Sesión' : 'Registrar mi Institución'}
        </h2>
      </BoxReveal>

      <BoxReveal boxColor="var(--color-primary)" duration={0.3} className="pb-2">
        <p className="text-text-secondary text-sm max-w-sm mx-auto text-center">
          {isLogin
            ? 'Selecciona tu institución e ingresa tus credenciales.'
            : 'Crea tu cuenta como director para gestionar tu institución educativa.'}
        </p>
      </BoxReveal>

      {isLogin ? (
        <form onSubmit={loginForm.handleSubmit(onLoginSubmit)} className="space-y-3">
          <div className="flex flex-col gap-1.5">
            <label htmlFor="login-institution" className="text-sm font-medium text-text">
              Institución Educativa
            </label>
            {instLoading ? (
              <div className="h-10 rounded-md border bg-background px-3 flex items-center text-sm text-text-secondary">
                Cargando instituciones...
              </div>
            ) : instError ? (
              <div className="h-10 rounded-md border border-danger/50 bg-danger/5 px-3 flex items-center text-sm text-danger">
                Error al cargar instituciones. Verifica la conexión.
              </div>
            ) : (
              <select
                id="login-institution"
                value={selectedInst}
                onChange={(e) => setSelectedInst(e.target.value)}
                className="shadow-input flex h-10 w-full rounded-md border bg-background px-3 py-2 text-sm text-text transition-shadow focus-visible:ring-[2px] focus-visible:ring-primary focus-visible:outline-none"
              >
                {institutions?.map((inst) => (
                  <option key={inst.id} value={inst.code}>
                    {inst.name} ({inst.code})
                  </option>
                ))}
              </select>
            )}
          </div>

          <AnimatedInput
            id="login-email"
            label="Email"
            type="email"
            placeholder="tu@email.com"
            error={loginForm.formState.errors.email?.message}
            ref={loginEmail.ref}
            name={loginEmail.name}
            onChange={loginEmail.onChange}
            onBlur={loginEmail.onBlur}
          />
          <AnimatedInput
            id="login-password"
            label="Contraseña"
            type="password"
            placeholder="••••••••"
            error={loginForm.formState.errors.password?.message}
            ref={loginPassword.ref}
            name={loginPassword.name}
            onChange={loginPassword.onChange}
            onBlur={loginPassword.onBlur}
          />

          {loginMutation.isError && (
            <p className="text-sm text-danger" role="alert">
              {(loginMutation.error as Error)?.message || 'Credenciales inválidas'}
            </p>
          )}

          <BoxReveal width="100%" boxColor="var(--color-primary)" duration={0.3} overflow="visible">
            <ClickPowerUp
              type="submit"
              disabled={loginForm.formState.isSubmitting}
              className="w-full"
            >
              {loginMutation.isPending ? 'Iniciando...' : 'Iniciar Sesión'}
            </ClickPowerUp>
          </BoxReveal>

          <BoxReveal boxColor="var(--color-primary)" duration={0.3}>
            <div className="mt-2 text-center">
              <button
                type="button"
                onClick={(e) => {
                  e.preventDefault();
                  navigate('/forgot-password');
                }}
                className="text-sm text-primary hover:text-primary/80 transition-colors outline-hidden"
              >
                ¿Olvidaste tu contraseña?
              </button>
            </div>
          </BoxReveal>

          <BoxReveal boxColor="var(--color-primary)" duration={0.3}>
            <div className="mt-4 text-center">
              <span className="text-sm text-text-secondary">
                ¿Eres director de una IE?{' '}
                <button
                  type="button"
                  onClick={handleToggleMode}
                  className="text-primary font-medium hover:text-primary/80 transition-colors outline-hidden"
                >
                  Registra tu cuenta
                </button>
              </span>
            </div>
          </BoxReveal>
        </form>
      ) : (
        <form onSubmit={registerForm.handleSubmit(onRegisterSubmit)} className="space-y-3">
          <div className="grid grid-cols-2 gap-3">
            <AnimatedInput
              id="register-firstName"
              label="Nombre"
              type="text"
              placeholder="Juan"
              error={registerForm.formState.errors.firstName?.message}
              ref={regFirstName.ref}
              name={regFirstName.name}
              onChange={regFirstName.onChange}
              onBlur={regFirstName.onBlur}
            />
            <AnimatedInput
              id="register-lastName"
              label="Apellido"
              type="text"
              placeholder="Pérez"
              error={registerForm.formState.errors.lastName?.message}
              ref={regLastName.ref}
              name={regLastName.name}
              onChange={regLastName.onChange}
              onBlur={regLastName.onBlur}
            />
          </div>
          <AnimatedInput
            id="register-email"
            label="Email"
            type="email"
            placeholder="tu@email.com"
            error={registerForm.formState.errors.email?.message}
            ref={regEmail.ref}
            name={regEmail.name}
            onChange={regEmail.onChange}
            onBlur={regEmail.onBlur}
          />
          <div className="grid grid-cols-2 gap-3">
            <AnimatedInput
              id="register-password"
              label="Contraseña"
              type="password"
              placeholder="Mín. 8 caracteres"
              error={registerForm.formState.errors.password?.message}
              ref={regPassword.ref}
              name={regPassword.name}
              onChange={regPassword.onChange}
              onBlur={regPassword.onBlur}
            />
            <AnimatedInput
              id="register-confirmPassword"
              label="Confirmar"
              type="password"
              placeholder="Repite la contraseña"
              error={registerForm.formState.errors.confirmPassword?.message}
              ref={regConfirmPassword.ref}
              name={regConfirmPassword.name}
              onChange={regConfirmPassword.onChange}
              onBlur={regConfirmPassword.onBlur}
            />
          </div>

          <hr className="border-border my-1" />
          <p className="text-xs font-medium text-text-secondary uppercase tracking-wide">Datos de la Institución</p>

          <AnimatedInput
            id="register-inst-name"
            label="Nombre de la IE"
            type="text"
            placeholder="Colegio San Martín"
            error={registerForm.formState.errors.institutionName?.message}
            ref={regInstName.ref}
            name={regInstName.name}
            onChange={regInstName.onChange}
            onBlur={regInstName.onBlur}
          />
          <div className="grid grid-cols-2 gap-3">
            <AnimatedInput
              id="register-inst-code"
              label="Código"
              type="text"
              placeholder="CSM-001"
              error={registerForm.formState.errors.institutionCode?.message}
              ref={regInstCode.ref}
              name={regInstCode.name}
              onChange={regInstCode.onChange}
              onBlur={regInstCode.onBlur}
            />
            <AnimatedInput
              id="register-inst-address"
              label="Dirección"
              type="text"
              placeholder="Av. Principal 123"
              error={registerForm.formState.errors.institutionAddress?.message}
              ref={regInstAddress.ref}
              name={regInstAddress.name}
              onChange={regInstAddress.onChange}
              onBlur={regInstAddress.onBlur}
            />
          </div>

          {registerMutation.isError && (
            <p className="text-sm text-danger" role="alert">
              {(registerMutation.error as Error)?.message || 'Error al registrar'}
            </p>
          )}

          {registerMutation.isSuccess && (
            <p className="text-sm text-success" role="alert">
              Institución registrada exitosamente. Ahora puedes iniciar sesión.
            </p>
          )}

          <BoxReveal width="100%" boxColor="var(--color-primary)" duration={0.3} overflow="visible">
            <ClickPowerUp
              type="submit"
              disabled={registerForm.formState.isSubmitting}
              className="w-full"
            >
              {registerMutation.isPending ? 'Registrando...' : 'Registrar Institución'}
            </ClickPowerUp>
          </BoxReveal>

          <BoxReveal boxColor="var(--color-primary)" duration={0.3}>
            <div className="mt-4 text-center">
              <span className="text-sm text-text-secondary">
                ¿Ya tienes cuenta?{' '}
                <button
                  type="button"
                  onClick={handleToggleMode}
                  className="text-primary font-medium hover:text-primary/80 transition-colors outline-hidden"
                >
                  Inicia sesión
                </button>
              </span>
            </div>
          </BoxReveal>
        </form>
      )}
    </section>
  );
}


