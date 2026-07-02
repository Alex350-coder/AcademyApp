import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/shared/components/Button';

export function FinalCTASection() {
  const navigate = useNavigate();

  return (
    <section id="cta" className="relative py-16 sm:py-20 overflow-hidden">
      <div className="absolute inset-0 bg-gradient-to-br from-primary via-secondary to-accent opacity-90" />
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_50%,rgba(255,255,255,0.1),transparent_60%)]" />

      <div className="relative max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5 }}
        >
          <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-white leading-tight">
            ¿Eres director de una institución educativa?
          </h2>
          <p className="mt-4 text-base sm:text-lg text-white/80 max-w-xl mx-auto">
            Registra tu institución y gestiona todo desde un solo lugar: notas,
            asistencias, matrículas y comunicaciones.
          </p>
          <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-3">
            <Button onClick={() => navigate('/register')}>
              Registrar mi IE
            </Button>
            <Button
              variant="secondary"
              onClick={() => navigate('/login')}
            >
              Ya tengo cuenta
            </Button>
          </div>
          <p className="mt-4 text-xs text-white/60">
            Crea tu cuenta como director en menos de 2 minutos.
          </p>
        </motion.div>
      </div>
    </section>
  );
}
