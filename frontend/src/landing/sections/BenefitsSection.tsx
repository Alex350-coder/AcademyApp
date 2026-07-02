import { motion } from 'framer-motion';
import { benefits } from '@/landing/data/content';

const containerVariants = {
  hidden: {},
  visible: {
    transition: { staggerChildren: 0.08 },
  },
};

const cardVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.4 },
  },
};

export function BenefitsSection() {
  return (
    <section id="benefits" className="py-16 sm:py-20 bg-background/60 backdrop-blur-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          className="text-center max-w-2xl mx-auto mb-12"
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5 }}
        >
          <h2 className="text-2xl sm:text-3xl font-bold text-text">
            Todo lo que necesitas para gestionar tu institución
          </h2>
          <p className="mt-3 text-text-secondary text-sm sm:text-base">
            Herramientas diseñadas para cada rol de tu comunidad educativa.
          </p>
        </motion.div>

        <motion.div
          className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6"
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true }}
        >
          {benefits.map((benefit) => (
            <motion.div
              key={benefit.title}
              className="bg-surface rounded-lg border border-border p-5 hover:shadow-elevation-2 transition-shadow"
              variants={cardVariants}
            >
              <span className="text-2xl">{benefit.icon}</span>
              <h3 className="mt-3 text-base font-semibold text-text">
                {benefit.title}
              </h3>
              <p className="mt-1.5 text-sm text-text-secondary leading-relaxed">
                {benefit.description}
              </p>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
}
