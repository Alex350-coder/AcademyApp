import { useRef, useEffect, useState, useCallback } from 'react';
import { motion, useInView, useReducedMotion } from 'framer-motion';
import { mockupData } from '@/landing/data/content';

interface AnimatedValueProps {
  value: number;
  suffix?: string;
}

function AnimatedValue({ value, suffix = '' }: AnimatedValueProps) {
  const ref = useRef<HTMLSpanElement>(null);
  const inView = useInView(ref, { once: true });
  const prefersReduced = useReducedMotion();
  const [displayed, setDisplayed] = useState(value);

  const animateValue = useCallback(() => {
    if (prefersReduced) {
      setDisplayed(value);
      return;
    }
    const duration = 2000;
    const start = performance.now();
    const update = (now: number) => {
      const elapsed = now - start;
      const progress = Math.min(elapsed / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      setDisplayed(Math.floor(eased * value));
      if (progress < 1) {
        requestAnimationFrame(update);
      } else {
        setDisplayed(value);
      }
    };
    requestAnimationFrame(update);
  }, [value, prefersReduced]);

  useEffect(() => {
    if (inView) animateValue();
  }, [inView, animateValue]);

  return <span ref={ref}>{displayed.toLocaleString()}{suffix}</span>;
}

interface BarProps {
  value: number;
  color: string;
  label: string;
  index: number;
}

function Bar({ value, color, label, index }: BarProps) {
  const prefersReduced = useReducedMotion();

  return (
    <div className="flex items-center gap-3">
      <span className="text-xs text-muted w-20 sm:w-24 text-right truncate">{label}</span>
      <div className="flex-1 h-3 bg-surface-hover rounded-full overflow-hidden">
        <motion.div
          className={`h-full rounded-full ${color}`}
          initial={{ width: 0 }}
          whileInView={{ width: `${value}%` }}
          viewport={{ once: true }}
          transition={{ duration: 1.2, delay: index * 0.15, ease: 'easeOut' }}
          style={prefersReduced ? { width: `${value}%` } : undefined}
        />
      </div>
      <span className="text-xs font-medium text-text w-8 text-right">{value}%</span>
    </div>
  );
}

export function FloatingMockup() {
  const prefersReduced = useReducedMotion();

  const containerVariants = prefersReduced
    ? {}
    : {
        animate: {
          y: [0, -6, 0],
          transition: { duration: 5, repeat: Infinity, ease: 'easeInOut' as const },
        },
      };

  return (
    <motion.div
      className="relative mx-auto max-w-3xl"
      {...(!prefersReduced ? containerVariants : {})}
    >
      <div className="bg-surface rounded-xl shadow-elevation-3 border border-border overflow-hidden">
        {/* Browser chrome */}
        <div className="flex items-center gap-1.5 px-4 py-3 bg-background border-b border-border">
          <span className="w-3 h-3 rounded-full bg-danger" />
          <span className="w-3 h-3 rounded-full bg-warning" />
          <span className="w-3 h-3 rounded-full bg-success" />
          <span className="ml-3 text-xs text-muted bg-surface-hover px-3 py-1 rounded-md flex-1 max-w-md truncate">
            app.academiasaas.com/dashboard
          </span>
        </div>

        {/* Mockup content */}
        <div className="p-5 sm:p-6 space-y-6">
          {/* Header */}
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-text">Panel del Director</h3>
            <span className="text-xs text-success bg-success/10 px-2 py-0.5 rounded-full font-medium">
              Actualizado ahora
            </span>
          </div>

          {/* Stats grid */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            {mockupData.stats.map((stat, i) => (
              <motion.div
                key={stat.label}
                className="bg-background rounded-lg p-3 border border-border"
                initial={{ opacity: 0, y: 12 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.1, duration: 0.4 }}
              >
                <p className="text-xs text-muted mb-0.5">{stat.label}</p>
                <p className="text-xl sm:text-2xl font-bold text-text">
                  <AnimatedValue value={stat.value} suffix={stat.suffix} />
                </p>
              </motion.div>
            ))}
          </div>

          {/* Two-column lower section */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Alumnos en Riesgo */}
            <div>
              <h4 className="text-xs font-semibold text-text mb-2 flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-danger" />
                Alumnos en Riesgo
              </h4>
              <div className="space-y-2">
                {mockupData.atRiskStudents.map((s, i) => (
                  <motion.div
                    key={s.name}
                    className="flex items-center justify-between bg-background rounded-lg px-3 py-2"
                    initial={{ opacity: 0, x: -8 }}
                    whileInView={{ opacity: 1, x: 0 }}
                    viewport={{ once: true }}
                    transition={{ delay: 0.3 + i * 0.1, duration: 0.3 }}
                  >
                    <div className="flex items-center gap-2">
                      <span
                        className={`w-2 h-2 rounded-full ${
                          s.status === 'critical' ? 'bg-danger' : 'bg-warning'
                        }`}
                      />
                      <span className="text-sm text-text">{s.name}</span>
                    </div>
                    <span
                      className={`text-xs font-medium ${
                        s.status === 'critical' ? 'text-danger' : 'text-warning'
                      }`}
                    >
                      {s.average}%
                    </span>
                  </motion.div>
                ))}
              </div>
            </div>

            {/* Course Performance Bars */}
            <div>
              <h4 className="text-xs font-semibold text-text mb-2">
                Rendimiento por Curso
              </h4>
              <div className="space-y-2">
                {mockupData.coursePerformance.map((course, i) => (
                  <Bar
                    key={course.course}
                    label={course.course}
                    value={course.value}
                    color={course.color}
                    index={i}
                  />
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Subtle glow behind */}
      <div className="absolute -inset-4 bg-gradient-to-r from-primary/5 via-accent/5 to-secondary/5 rounded-3xl blur-3xl -z-10" />
    </motion.div>
  );
}
