import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { navLinks } from '@/landing/data/content';
import { ThemeToggle } from '@/shared/components/ThemeToggle';
import { Button } from '@/shared/components/Button';

export function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleNavClick = (href: string) => {
    setMobileOpen(false);
    const id = href.replace('#', '');
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <nav
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-200 ${
        scrolled
          ? 'bg-surface/80 backdrop-blur-lg shadow-elevation-1'
          : 'bg-transparent'
      }`}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link
            to="/"
            className="text-xl font-bold text-text flex items-center gap-1.5"
          >
            <span className="w-7 h-7 rounded-md bg-gradient-to-br from-primary to-secondary flex items-center justify-center text-white text-xs font-bold">
              A
            </span>
            <span>
              Academia <span className="text-primary">SaaS</span>
            </span>
          </Link>

          <div className="hidden md:flex items-center gap-6">
            {navLinks.map((link) => (
              <button
                key={link.href}
                onClick={() => handleNavClick(link.href)}
                className="text-text-secondary hover:text-text transition-colors text-sm font-medium"
              >
                {link.label}
              </button>
            ))}
            <ThemeToggle />
            <Button
              variant="secondary"
              size="sm"
              onClick={() => navigate('/register')}
            >
              Registrar mi IE
            </Button>
            <Button
              size="sm"
              onClick={() => navigate('/login')}
            >
              Iniciar Sesión
            </Button>
          </div>

          <button
            className="md:hidden p-2 rounded-md text-text-secondary hover:text-text hover:bg-surface-hover transition-colors"
            onClick={() => setMobileOpen(!mobileOpen)}
            aria-label={mobileOpen ? 'Cerrar menú' : 'Abrir menú'}
          >
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
              {mobileOpen ? (
                <>
                  <line x1="18" y1="6" x2="6" y2="18" />
                  <line x1="6" y1="6" x2="18" y2="18" />
                </>
              ) : (
                <>
                  <line x1="4" y1="6" x2="20" y2="6" />
                  <line x1="4" y1="12" x2="20" y2="12" />
                  <line x1="4" y1="18" x2="20" y2="18" />
                </>
              )}
            </svg>
          </button>
        </div>
      </div>

      {mobileOpen && (
        <div className="md:hidden bg-surface border-t border-border animate-fadeIn">
          <div className="px-4 py-3 space-y-3">
            {navLinks.map((link) => (
              <button
                key={link.href}
                onClick={() => handleNavClick(link.href)}
                className="block w-full text-left text-text-secondary hover:text-text transition-colors text-sm py-2"
              >
                {link.label}
              </button>
            ))}
            <div className="flex items-center justify-between pt-1">
              <ThemeToggle />
            </div>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => {
                setMobileOpen(false);
                navigate('/register');
              }}
            >
              Registrar mi IE
            </Button>
            <Button
              size="sm"
              onClick={() => {
                setMobileOpen(false);
                navigate('/login');
              }}
            >
              Iniciar Sesión
            </Button>
          </div>
        </div>
      )}
    </nav>
  );
}
