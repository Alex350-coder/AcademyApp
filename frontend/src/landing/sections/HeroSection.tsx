import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { heroContent } from '@/landing/data/content';
import { FloatingMockup } from '@/landing/components/FloatingMockup';
import { Button } from '@/shared/components/Button';

export function HeroSection() {
  const navigate = useNavigate();
  const handleScrollTo = (href: string) => {
    const id = href.replace('#', '');
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <section className="relative min-h-screen flex items-center pt-24 pb-16 sm:pb-20 overflow-hidden">
      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 w-full">
        <motion.div
          className="text-center max-w-3xl mx-auto"
          initial={{ opacity: 0, y: 24 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/5 border border-primary/10 text-primary text-xs font-medium mb-6">
            <span className="w-1.5 h-1.5 rounded-full bg-primary" />
            Plataforma integral de gestión educativa
          </div>
          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-bold text-text leading-tight tracking-tight">
            {heroContent.title}
          </h1>
          <p className="mt-4 text-base sm:text-lg text-text-secondary max-w-2xl mx-auto leading-relaxed">
            {heroContent.subtitle}
          </p>
          <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-3">
            <Button onClick={() => navigate('/register')}>
              Registrar mi IE
            </Button>
            <Button
              variant="secondary"
              onClick={() => handleScrollTo(heroContent.ctaSecondary.href)}
            >
              {heroContent.ctaSecondary.text}
            </Button>
          </div>
        </motion.div>

        <motion.div
          className="mt-12 sm:mt-16"
          initial={{ opacity: 0, y: 32 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.7, delay: 0.3 }}
        >
          <FloatingMockup />
        </motion.div>
      </div>
    </section>
  );
}
