import { useCallback } from 'react';
import { Navigate, useNavigate, useLocation } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { GradientDots } from '@/components/ui/gradient-dots';
import { AnimatedForm } from '../components/AnimatedForm';
import { Ripple, EducationOrbitDisplay } from '../components/auth-decorations';
import { AuthNavbar } from '../components/AuthNavbar';
import { useAuthStore, getDashboardPath } from '@/shared/store/useAuthStore';

export default function AuthPage() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const navigate = useNavigate();
  const location = useLocation();
  const mode = location.pathname === '/register' ? 'register' : 'login';
  const isLogin = mode === 'login';

  const onToggleMode = useCallback(() => {
    navigate(isLogin ? '/register' : '/login');
  }, [isLogin, navigate]);

  if (isAuthenticated) {
    return <Navigate to={getDashboardPath(useAuthStore.getState().user)} replace />;
  }

  const panelSlide = { type: 'tween' as const, duration: 0.25, ease: 'easeInOut' as const };

  return (
    <div className="min-h-screen bg-background flex items-center justify-center overflow-hidden relative">
      <GradientDots
        className="pointer-events-none"
        backgroundColor="transparent"
        duration={30}
        blobOpacity={0.08}
      />

      <AuthNavbar backTo="/" />

      <div className="relative z-10 w-full max-w-5xl mx-auto px-4 overflow-hidden rounded-2xl border border-border bg-surface/60 backdrop-blur-md shadow-elevation-2">
        {/* Mobile: full-width form */}
        <div className="lg:hidden overflow-y-auto [&::-webkit-scrollbar]:w-0">
          <div className="flex items-start justify-center min-h-[600px] py-8">
            <AnimatedForm mode={mode} onToggleMode={onToggleMode} />
          </div>
        </div>

        {/* Desktop: two fixed panels with content swap */}
        <div className="hidden lg:flex" style={{ height: '680px' }}>
          {/* Left panel */}
          <div
            className={`w-1/2 h-full bg-gradient-to-br from-primary/5 via-accent/5 to-background ${
              isLogin ? 'overflow-hidden' : 'overflow-y-auto [&::-webkit-scrollbar]:w-0'
            }`}
          >
            <div className="relative w-full min-h-full">
              <AnimatePresence mode="popLayout">
                {isLogin ? (
                  <motion.div
                    key="left-anim"
                    className="absolute inset-0 flex items-center justify-center"
                    initial={{ opacity: 0, x: 30 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: 30 }}
                    transition={panelSlide}
                  >
                    <Ripple mainCircleSize={80} />
                    <EducationOrbitDisplay />
                  </motion.div>
                ) : (
                  <motion.div
                    key="left-form"
                    className="absolute inset-0 flex items-start justify-center py-8"
                    initial={{ opacity: 0, x: 30 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: 30 }}
                    transition={panelSlide}
                  >
                    <AnimatedForm mode={mode} onToggleMode={onToggleMode} />
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>

          {/* Right panel */}
          <div
            className={`w-1/2 h-full ${
              isLogin ? 'overflow-y-auto [&::-webkit-scrollbar]:w-0' : 'overflow-hidden'
            }`}
          >
            <div className="relative w-full min-h-full">
              <AnimatePresence mode="popLayout">
                {isLogin ? (
                  <motion.div
                    key="right-form"
                    className="absolute inset-0 flex items-start justify-center py-8"
                    initial={{ opacity: 0, x: -30 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -30 }}
                    transition={panelSlide}
                  >
                    <AnimatedForm mode={mode} onToggleMode={onToggleMode} />
                  </motion.div>
                ) : (
                  <motion.div
                    key="right-anim"
                    className="absolute inset-0 flex items-center justify-center bg-gradient-to-bl from-primary/5 via-accent/5 to-background"
                    initial={{ opacity: 0, x: -30 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -30 }}
                    transition={panelSlide}
                  >
                    <Ripple mainCircleSize={80} />
                    <EducationOrbitDisplay />
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
