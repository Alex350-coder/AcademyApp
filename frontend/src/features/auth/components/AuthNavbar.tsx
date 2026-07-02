import { Link } from 'react-router-dom';
import { ThemeToggle } from '@/shared/components/ThemeToggle';

interface AuthNavbarProps {
  backTo?: string;
}

export function AuthNavbar({ backTo = '/' }: AuthNavbarProps) {
  return (
    <nav className="absolute top-0 left-0 right-0 z-20 flex items-center justify-between px-4 py-3">
      <Link
        to={backTo}
        className="inline-flex items-center gap-1.5 text-sm text-text-secondary hover:text-text transition-colors"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <line x1="19" y1="12" x2="5" y2="12" />
          <polyline points="12 19 5 12 12 5" />
        </svg>
        Volver
      </Link>
      <ThemeToggle />
    </nav>
  );
}
