import { useRef, useId, useEffect, type CSSProperties } from 'react';
import { animate, useMotionValue, type AnimationPlaybackControls } from 'framer-motion';

function mapRange(
  value: number,
  fromLow: number,
  fromHigh: number,
  toLow: number,
  toHigh: number,
): number {
  if (fromLow === fromHigh) return toLow;
  const percentage = (value - fromLow) / (fromHigh - fromLow);
  return toLow + percentage * (toHigh - toLow);
}

interface EtheralShadowProps {
  color?: string;
  animationScale?: number;
  animationSpeed?: number;
  noiseOpacity?: number;
  noiseScale?: number;
  className?: string;
  style?: CSSProperties;
}

export function EtheralShadow({
  color = 'rgba(37, 99, 235, 0.3)',
  animationScale = 60,
  animationSpeed = 70,
  noiseOpacity = 0.5,
  noiseScale = 1,
  className,
  style,
}: EtheralShadowProps) {
  const id = useId().replace(/:/g, '');
  const maskId = `${id}-mask`;
  const feColorMatrixRef = useRef<SVGFEColorMatrixElement>(null);
  const hueRotateMotionValue = useMotionValue(180);
  const hueRotateAnimation = useRef<AnimationPlaybackControls | null>(null);

  const displacementScale = mapRange(animationScale, 1, 100, 20, 100);
  const animationDuration = mapRange(animationSpeed, 1, 100, 1000, 50);

  useEffect(() => {
    if (feColorMatrixRef.current) {
      if (hueRotateAnimation.current) hueRotateAnimation.current.stop();
      hueRotateMotionValue.set(0);
      hueRotateAnimation.current = animate(hueRotateMotionValue, 360, {
        duration: animationDuration / 25,
        repeat: Infinity,
        repeatType: 'loop',
        ease: 'linear',
        onUpdate: (value: number) => {
          feColorMatrixRef.current?.setAttribute('values', String(value));
        },
      });
      return () => hueRotateAnimation.current?.stop();
    }
  }, [animationDuration, hueRotateMotionValue]);

  return (
    <div
      className={className}
      style={{
        overflow: 'hidden',
        position: 'relative',
        width: '100%',
        height: '100%',
        ...style,
      }}
    >
      <div
        style={{
          position: 'absolute',
          inset: -displacementScale,
          filter: `url(#${id}) blur(4px)`,
        }}
      >
        <svg style={{ position: 'absolute', width: 0, height: 0 }}>
          <defs>
            <filter id={id}>
              <feTurbulence
                result="undulation"
                numOctaves="2"
                baseFrequency={`${mapRange(animationScale, 1, 100, 0.008, 0.003)} ${mapRange(animationScale, 1, 100, 0.004, 0.002)}`}
                seed="0"
                type="turbulence"
              />
              <feColorMatrix
                ref={feColorMatrixRef}
                in="undulation"
                type="hueRotate"
                values="180"
              />
              <feColorMatrix
                in="dist"
                result="circulation"
                type="matrix"
                values="4 0 0 0 1  4 0 0 0 1  4 0 0 0 1  1 0 0 0 0"
              />
              <feDisplacementMap
                in="SourceGraphic"
                in2="circulation"
                scale={displacementScale}
                result="dist"
              />
              <feDisplacementMap
                in="dist"
                in2="undulation"
                scale={displacementScale}
                result="output"
              />
            </filter>
            <mask id={maskId}>
              <circle cx="50%" cy="50%" r="45%" fill="white" />
              <ellipse cx="35%" cy="40%" rx="25%" ry="30%" fill="white" />
              <ellipse cx="65%" cy="55%" rx="20%" ry="25%" fill="white" />
              <ellipse cx="50%" cy="35%" rx="30%" ry="20%" fill="white" />
              <ellipse cx="45%" cy="65%" rx="30%" ry="20%" fill="white" />
            </mask>
          </defs>
        </svg>
        <div
          style={{
            position: 'absolute',
            inset: 0,
            backgroundColor: color,
            mask: `url(#${maskId})`,
            WebkitMask: `url(#${maskId})`,
            maskSize: 'cover',
            WebkitMaskSize: 'cover',
          }}
        />
      </div>

      <div
        style={{
          position: 'absolute',
          inset: 0,
          background: `radial-gradient(ellipse at 50% 50%, transparent 0%, var(--color-background) 100%)`,
          opacity: 0.6,
          pointerEvents: 'none',
        }}
      />

      {noiseOpacity > 0 && (
        <div
          style={{
            position: 'absolute',
            inset: 0,
            backgroundImage: `url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E")`,
            backgroundSize: `${noiseScale * 200}px ${noiseScale * 200}px`,
            backgroundRepeat: 'repeat',
            opacity: noiseOpacity / 4,
            pointerEvents: 'none',
          }}
        />
      )}
    </div>
  );
}
