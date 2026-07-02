import { type ReactNode } from 'react';

type BadgeVariant = 'default' | 'success' | 'warning' | 'danger' | 'info';

interface BadgeProps {
  variant?: BadgeVariant;
  children: ReactNode;
  className?: string;
}

const variantClasses: Record<BadgeVariant, string> = {
  default: 'bg-surface-hover text-text border border-border',
  success: 'bg-success-bg text-success border border-success/20',
  warning: 'bg-warning-bg text-warning border border-warning/20',
  danger: 'bg-danger-bg text-danger border border-danger/20',
  info: 'bg-primary/10 text-primary border border-primary/20',
};

export function Badge({ variant = 'default', children, className = '' }: BadgeProps) {
  return (
    <span
      className={`inline-flex items-center gap-1 px-2.5 py-0.5 text-xs font-medium rounded-full ${variantClasses[variant]} ${className}`}
    >
      {children}
    </span>
  );
}
