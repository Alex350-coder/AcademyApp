import { motion } from 'framer-motion';
import { FloatingMockup } from '@/landing/components/FloatingMockup';

export function DashboardPreviewSection() {
  return (
    <section className="py-16 sm:py-20 bg-background/60 backdrop-blur-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          className="text-center max-w-2xl mx-auto mb-12"
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5 }}
        >
          <h2 className="text-2xl sm:text-3xl font-bold text-text">
            Tu directorio académico en un vistazo
          </h2>
          <p className="mt-3 text-text-secondary text-sm sm:text-base">
            Datos en tiempo real, alertas inteligentes y reportes automáticos en
            un solo panel.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          whileInView={{ opacity: 1, scale: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
        >
          <FloatingMockup />
        </motion.div>
      </div>
    </section>
  );
}
