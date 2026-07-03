import { type ReactNode, memo, useEffect, useRef } from 'react';
import { motion, useAnimation, useInView } from 'framer-motion';
import {
  GraduationCap,
  BookOpen,
  Pencil,
  Lightbulb,
  Award,
  School,
  Brain,
  Globe,
} from 'lucide-react';
import { cn } from '@/lib/utils';

export const BoxReveal = memo(function BoxReveal({
  children,
  width = 'fit-content',
  boxColor,
  duration,
  overflow = 'hidden',
  className,
}: {
  children: ReactNode;
  width?: string;
  boxColor?: string;
  duration?: number;
  overflow?: string;
  className?: string;
}) {
  const mainControls = useAnimation();
  const slideControls = useAnimation();
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true });

  useEffect(() => {
    if (isInView) {
      slideControls.start('visible');
      mainControls.start('visible');
    } else {
      slideControls.start('hidden');
      mainControls.start('hidden');
    }
  }, [isInView, mainControls, slideControls]);

  return (
    <section ref={ref} className={cn('relative', className)} style={{ width, overflow }}>
      <motion.div
        variants={{
          hidden: { opacity: 0, y: 75 },
          visible: { opacity: 1, y: 0 },
        }}
        initial="hidden"
        animate={mainControls}
        transition={{ duration: duration ?? 0.5, delay: 0.25 }}
      >
        {children}
      </motion.div>
      <motion.div
        variants={{ hidden: { left: 0 }, visible: { left: '100%' } }}
        initial="hidden"
        animate={slideControls}
        transition={{ duration: duration ?? 0.5, ease: 'easeIn' }}
        style={{
          position: 'absolute',
          top: 4,
          bottom: 4,
          left: 0,
          right: 0,
          zIndex: 20,
          background: boxColor ?? 'var(--color-primary)',
          borderRadius: 4,
        }}
      />
    </section>
  );
});

export const Ripple = memo(function Ripple({
  mainCircleSize = 210,
  mainCircleOpacity = 0.24,
  numCircles = 11,
  className = '',
}: {
  mainCircleSize?: number;
  mainCircleOpacity?: number;
  numCircles?: number;
  className?: string;
}) {
  return (
      <div
        className={cn(
          'absolute inset-0 flex items-center justify-center',
          '[mask-image:linear-gradient(to_bottom,black,transparent)]',
          className,
        )}
      >
        {Array.from({ length: numCircles }, (_, i) => {
          const size = mainCircleSize + i * 70;
          const opacity = mainCircleOpacity - i * 0.03;
          const animationDelay = `${i * 0.06}s`;
          const borderStyle = i === numCircles - 1 ? 'dashed' : 'solid';

          return (
            <span
              key={i}
              className="absolute animate-ripple rounded-full border"
              style={{
                width: `${size}px`,
                height: `${size}px`,
                opacity,
                animationDelay,
                borderStyle,
                borderWidth: '1px',
                borderColor: 'var(--color-primary)',
                backgroundColor: 'var(--color-primary)',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
              }}
            />
          );
        })}
      </div>
  );
});

export const OrbitingCircles = memo(function OrbitingCircles({
  className,
  children,
  reverse = false,
  duration = 20,
  delay = 10,
  radius = 50,
  path = true,
}: {
  className?: string;
  children: ReactNode;
  reverse?: boolean;
  duration?: number;
  delay?: number;
  radius?: number;
  path?: boolean;
}) {
  return (
    <>
      {path && (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          version="1.1"
          className="pointer-events-none absolute inset-0 size-full"
        >
          <circle
            className="stroke-primary/10 stroke-1"
            cx="50%"
            cy="50%"
            r={radius}
            fill="none"
          />
        </svg>
      )}
      <div
        style={
          {
            '--duration': duration,
            '--radius': radius,
            '--delay': -delay,
          } as React.CSSProperties
        }
        className={cn(
          'absolute flex size-full transform-gpu animate-orbit items-center justify-center rounded-full border bg-primary/5 [animation-delay:calc(var(--delay)*1000ms)]',
          { '[animation-direction:reverse]': reverse },
          className,
        )}
      >
        {children}
      </div>
    </>
  );
});

interface EducationIconConfig {
  className?: string;
  duration?: number;
  delay?: number;
  radius?: number;
  path?: boolean;
  reverse?: boolean;
  icon: ReactNode;
}

export const EducationOrbitDisplay = memo(function EducationOrbitDisplay({
  icons,
}: {
  icons?: EducationIconConfig[];
}) {
  const defaultIcons: EducationIconConfig[] = [
    { icon: <GraduationCap size={36} />, radius: 110, duration: 25, path: false, reverse: false },
    { icon: <BookOpen size={30} />, radius: 110, duration: 25, delay: 8, path: false, reverse: false },
    { icon: <Pencil size={28} />, radius: 180, duration: 22, path: false, reverse: false },
    { icon: <Lightbulb size={32} />, radius: 180, duration: 22, delay: 11, path: false, reverse: false },
    { icon: <Award size={34} />, radius: 250, duration: 28, path: false, reverse: true },
    { icon: <School size={38} />, radius: 250, duration: 28, delay: 9, path: false, reverse: true },
    { icon: <Brain size={30} />, radius: 320, duration: 26, path: false, reverse: true },
    { icon: <Globe size={32} />, radius: 320, duration: 26, delay: 13, path: false, reverse: true },
  ];

  const iconList = icons ?? defaultIcons;

  return (
    <div className="relative flex h-full w-full flex-col items-center justify-center overflow-hidden">
      <span className="pointer-events-none whitespace-pre-wrap bg-gradient-to-b from-primary to-accent bg-clip-text text-center text-4xl font-bold leading-none text-transparent select-none">
        Academia SaaS
      </span>
      {iconList.map((iconConfig, index) => (
        <OrbitingCircles
          key={index}
          className={cn(
            'border-none bg-transparent text-primary',
            iconConfig.className,
          )}
          duration={iconConfig.duration}
          delay={iconConfig.delay}
          radius={iconConfig.radius}
          path={iconConfig.path}
          reverse={iconConfig.reverse}
        >
          <div className="flex items-center justify-center w-full h-full">
            {iconConfig.icon}
          </div>
        </OrbitingCircles>
      ))}
    </div>
  );
});
