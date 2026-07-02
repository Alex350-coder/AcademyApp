import { motion } from 'framer-motion';

type GradientDotsProps = React.ComponentProps<typeof motion.div> & {
  dotSize?: number;
  spacing?: number;
  duration?: number;
  blobOpacity?: number;
  backgroundColor?: string;
};

export function GradientDots({
  dotSize = 8,
  spacing = 28,
  duration = 70,
  blobOpacity = 0.12,
  backgroundColor = 'var(--color-background)',
  className,
  ...props
}: GradientDotsProps) {
  const hexSpacing = spacing * 1.732;

  const primary = `color-mix(in srgb, var(--color-primary) ${blobOpacity * 100}%, transparent)`;
  const secondary = `color-mix(in srgb, var(--color-secondary) ${blobOpacity * 100}%, transparent)`;
  const accent = `color-mix(in srgb, var(--color-accent) ${blobOpacity * 100}%, transparent)`;
  const dotColor = `color-mix(in srgb, var(--color-border) 70%, transparent)`;

  return (
    <motion.div
      className={`absolute inset-0 ${className ?? ''}`}
      style={{
        backgroundColor,
        backgroundImage: `
          radial-gradient(circle at 50% 50%, transparent 1.5px, ${dotColor} 0 1.5px, transparent 1.5px),
          radial-gradient(circle at 50% 50%, transparent 1.5px, ${dotColor} 0 1.5px, transparent 1.5px),
          radial-gradient(circle at 50% 50%, ${primary}, transparent 60%),
          radial-gradient(circle at 50% 50%, ${secondary}, transparent 60%),
          radial-gradient(ellipse at 50% 50%, ${accent}, transparent 65%)
        `,
        backgroundSize: `
          ${spacing}px ${hexSpacing}px,
          ${spacing}px ${hexSpacing}px,
          140% 140%,
          140% 140%,
          160% ${hexSpacing}px
        `,
        backgroundPosition: `
          0px 0px, ${spacing / 2}px ${hexSpacing / 2}px,
          0% 0%,
          0% 0%,
          0% 0px
        `,
      }}
      animate={{
        backgroundPosition: [
          `0px 0px, ${spacing / 2}px ${hexSpacing / 2}px, 0% 0%, 60% 40%, 0% 0px`,
          `0px 0px, ${spacing / 2}px ${hexSpacing / 2}px, 60% 40%, 0% 0%, 30% ${hexSpacing}px`,
          `0px 0px, ${spacing / 2}px ${hexSpacing / 2}px, 0% 0%, 60% 40%, 0% 0px`,
        ],
      }}
      transition={{
        backgroundPosition: {
          duration,
          ease: 'linear',
          repeat: Number.POSITIVE_INFINITY,
        },
      }}
      {...props}
    />
  );
}