import { motion } from 'framer-motion';
import { cn } from '@/lib/utils';
import { Button } from '@/shared/components/Button';

interface AuroraHeroProps {
  title?: string;
  description?: string;
  primaryAction?: {
    label: string;
    onClick: () => void;
  };
  secondaryAction?: {
    label: string;
    onClick: () => void;
  };
  className?: string;
  children?: React.ReactNode;
}

export function AuroraHero({
  title,
  description,
  primaryAction,
  secondaryAction,
  className,
  children,
}: AuroraHeroProps) {
  const titleWords = title?.split(' ') || [];

  return (
    <section
      className={cn(
        'relative w-full min-h-screen flex items-center justify-center overflow-hidden bg-background',
        className
      )}
      role="banner"
      aria-label="Hero section"
    >
      <div className="absolute inset-0 overflow-hidden opacity-40" aria-hidden="true">
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
      </div>

      <div
        className="absolute inset-0 pointer-events-none"
        style={{
          background: 'radial-gradient(ellipse at center, transparent 0%, var(--color-background) 100%)',
        }}
        aria-hidden="true"
      />

      {children ? (
        <div className="relative z-10 w-full">{children}</div>
      ) : (
        <div className="relative z-10 container mx-auto px-4 md:px-6 text-center">
          <motion.div
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 1.2, ease: 'easeOut' }}
            className="max-w-5xl mx-auto"
          >
            {title && (
              <h1 className="text-5xl sm:text-6xl md:text-8xl lg:text-9xl font-bold mb-8 tracking-tight">
                {titleWords.map((word, wordIndex) => (
                  <span key={wordIndex} className="inline-block mr-4 last:mr-0 mb-2">
                    {word.split('').map((letter, letterIndex) => (
                      <motion.span
                        key={`${wordIndex}-${letterIndex}`}
                        initial={{
                          y: 100,
                          opacity: 0,
                          filter: 'blur(8px)',
                        }}
                        animate={{
                          y: 0,
                          opacity: 1,
                          filter: 'blur(0px)',
                        }}
                        transition={{
                          delay: wordIndex * 0.1 + letterIndex * 0.03,
                          type: 'spring',
                          stiffness: 100,
                          damping: 15,
                        }}
                        whileHover={{
                          scale: 1.1,
                          transition: { duration: 0.2 },
                        }}
                        className="inline-block text-transparent bg-clip-text bg-gradient-to-br from-text via-text/90 to-text/70 cursor-default"
                        style={{
                          textShadow: '0 0 20px color-mix(in srgb, var(--color-primary) 30%, transparent)',
                        }}
                      >
                        {letter}
                      </motion.span>
                    ))}
                  </span>
                ))}
              </h1>
            )}

            {description && (
              <motion.p
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 1, delay: 0.6 }}
                className="text-lg sm:text-xl md:text-2xl text-muted mb-10 max-w-3xl mx-auto leading-relaxed"
              >
                {description}
              </motion.p>
            )}

            {(primaryAction || secondaryAction) && (
              <motion.div
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.8, delay: 1 }}
                className="flex flex-col sm:flex-row gap-4 justify-center items-center"
              >
                {primaryAction && (
                  <Button
                    onClick={primaryAction.onClick}
                    size="lg"
                    aria-label={primaryAction.label}
                  >
                    {primaryAction.label}
                  </Button>
                )}
                {secondaryAction && (
                  <Button
                    variant="secondary"
                    size="lg"
                    onClick={secondaryAction.onClick}
                    aria-label={secondaryAction.label}
                  >
                    {secondaryAction.label}
                  </Button>
                )}
              </motion.div>
            )}
          </motion.div>
        </div>
      )}
    </section>
  );
}
