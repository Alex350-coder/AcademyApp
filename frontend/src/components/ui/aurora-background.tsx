import { motion } from 'framer-motion';

export function AuroraBackground() {
  return (
    <div className="fixed inset-0 overflow-hidden pointer-events-none" aria-hidden="true">
      <motion.div
        className="absolute inset-[-100%]"
        style={{
          background: `
            repeating-linear-gradient(100deg,
              var(--color-primary) 5%,
              var(--color-accent) 12%,
              var(--color-secondary) 19%,
              var(--color-primary) 24%,
              var(--color-accent) 30%)
          `,
          backgroundSize: '300% 100%',
          filter: 'blur(80px)',
          opacity: 0.4,
        }}
        animate={{
          backgroundPosition: ['0% 50%', '100% 50%', '0% 50%'],
        }}
        transition={{
          duration: 20,
          repeat: Infinity,
          ease: 'linear',
        }}
      />
      <motion.div
        className="absolute inset-[-10px]"
        style={{
          background: `
            repeating-linear-gradient(100deg,
              color-mix(in srgb, var(--color-primary) 8%, transparent) 0%,
              color-mix(in srgb, var(--color-primary) 8%, transparent) 7%,
              transparent 10%,
              transparent 12%,
              color-mix(in srgb, var(--color-primary) 8%, transparent) 16%),
            repeating-linear-gradient(100deg,
              var(--color-primary) 5%,
              var(--color-accent) 12%,
              var(--color-secondary) 19%,
              var(--color-primary) 24%,
              var(--color-accent) 30%)
          `,
          backgroundSize: '200%, 100%',
          backgroundPosition: '50% 50%, 50% 50%',
          mixBlendMode: 'difference',
          opacity: 0.4,
        }}
        animate={{
          backgroundPosition: [
            '50% 50%, 50% 50%',
            '100% 50%, 150% 50%',
            '50% 50%, 50% 50%',
          ],
        }}
        transition={{
          duration: 15,
          repeat: Infinity,
          ease: 'linear',
        }}
      />
      <div
        className="absolute inset-0"
        style={{
          background: 'radial-gradient(ellipse at center, transparent 0%, var(--color-background) 100%)',
        }}
      />
    </div>
  );
}
