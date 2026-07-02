import { motion } from 'framer-motion';
import { testimonials } from '@/landing/data/content';

const cardVariants = {
  hidden: { opacity: 0, y: 16 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.4 },
  },
};

export function TestimonialsSection() {
  return (
    <section className="py-16 sm:py-20 bg-background/80 backdrop-blur-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          className="text-center max-w-2xl mx-auto mb-12"
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5 }}
        >
          <h2 className="text-2xl sm:text-3xl font-bold text-text">
            Lo que dicen las instituciones
          </h2>
          <p className="mt-3 text-text-secondary text-sm sm:text-base">
            Próximamente compartiremos las experiencias de nuestros primeros
            clientes.
          </p>
        </motion.div>

        <motion.div
          className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6"
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true }}
          variants={{
            hidden: {},
            visible: { transition: { staggerChildren: 0.1 } },
          }}
        >
          {testimonials.map((t) => (
            <motion.div
              key={t.institution}
              className="relative bg-surface rounded-lg border border-border p-6 overflow-hidden"
              variants={cardVariants}
            >
              {/* Gradient overlay to indicate placeholder */}
              <div className="absolute inset-0 bg-gradient-to-br from-primary/5 via-accent/5 to-transparent pointer-events-none" />

              <div className="relative">
                {/* Silhouette icon */}
                <div className="w-12 h-12 rounded-full bg-surface-hover flex items-center justify-center mb-4">
                  <svg
                    className="w-6 h-6 text-text-secondary"
                    fill="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
                  </svg>
                </div>

                <p className="text-sm text-text-secondary italic leading-relaxed">
                  &ldquo;{t.quote}&rdquo;
                </p>

                <div className="mt-4 pt-3 border-t border-border">
                  <p className="text-sm font-medium text-text">{t.institution}</p>
                  <span className="text-xs text-accent font-medium">Próximamente</span>
                </div>

                {/* Badge */}
                <div className="absolute top-3 right-3 bg-warning/10 text-warning text-[10px] font-semibold px-2 py-0.5 rounded-full border border-warning/20">
                  PRÓXIMAMENTE
                </div>
              </div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
}
