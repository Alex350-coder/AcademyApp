import { motion } from 'framer-motion';
import { features } from '@/landing/data/content';
import { FloatingMockup } from '@/landing/components/FloatingMockup';

export function FeaturesSection() {
  return (
    <section id="features" className="py-16 sm:py-20 bg-background/80 backdrop-blur-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          className="text-center max-w-2xl mx-auto mb-12"
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5 }}
        >
          <h2 className="text-2xl sm:text-3xl font-bold text-text">
            Funcionalidades diseñadas para cada rol
          </h2>
          <p className="mt-3 text-text-secondary text-sm sm:text-base">
            Herramientas específicas para directivos, secretarios, docentes y
            estudiantes.
          </p>
        </motion.div>

        <div className="space-y-16 sm:space-y-20">
          {features.map((feature, i) => (
            <motion.div
              key={feature.title}
              className={`flex flex-col ${
                feature.side === 'right' ? 'lg:flex-row-reverse' : 'lg:flex-row'
              } items-center gap-8 lg:gap-12`}
              initial={{ opacity: 0, y: 24 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: i * 0.1 }}
            >
              <div className="flex-1 w-full">
                <div
                  className={`inline-flex bg-gradient-to-br ${feature.color} text-white text-xs font-medium px-3 py-1 rounded-full mb-3`}
                >
                  {feature.side === 'left' ? 'Nuevo' : 'Popular'}
                </div>
                <h3 className="text-xl sm:text-2xl font-bold text-text">
                  {feature.title}
                </h3>
                <p className="mt-3 text-text-secondary text-sm sm:text-base leading-relaxed">
                  {feature.description}
                </p>
                <ul className="mt-4 space-y-2">
                  {feature.items.map((item) => (
                    <li key={item} className="flex items-center gap-2 text-sm text-text">
                      <svg
                        className="w-4 h-4 text-primary flex-shrink-0"
                        viewBox="0 0 20 20"
                        fill="currentColor"
                      >
                        <path
                          fillRule="evenodd"
                          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                          clipRule="evenodd"
                        />
                      </svg>
                      {item}
                    </li>
                  ))}
                </ul>
              </div>
              <motion.div
                className="flex-1 w-full max-w-lg"
                initial={{ opacity: 0, x: feature.side === 'left' ? 20 : -20 }}
                whileInView={{ opacity: 1, x: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.5, delay: 0.2 }}
              >
                <FloatingMockup />
              </motion.div>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
