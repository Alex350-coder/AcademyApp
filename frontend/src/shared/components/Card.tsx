import { type ReactNode } from 'react';

interface CardProps {
  children: ReactNode;
  className?: string;
  padding?: 'sm' | 'md' | 'lg';
  style?: React.CSSProperties;
}

const paddingClasses = {
  sm: 'p-3',
  md: 'p-5',
  lg: 'p-8',
};

export function Card({ children, className = '', padding = 'md', style }: CardProps) {
  return (
    <div
      className={`bg-surface border border-border rounded-lg shadow-elevation-1 ${paddingClasses[padding]} ${className}`}
      style={style}
    >
      {children}
    </div>
  );
}

export function CardHeader({ children, className = '' }: { children: ReactNode; className?: string }) {
  return <div className={`flex items-center justify-between mb-4 ${className}`}>{children}</div>;
}

export function CardTitle({ children, className = '' }: { children: ReactNode; className?: string }) {
  return <h3 className={`text-lg font-semibold text-text ${className}`}>{children}</h3>;
}

export function CardContent({ children, className = '' }: { children: ReactNode; className?: string }) {
  return <div className={className}>{children}</div>;
}
