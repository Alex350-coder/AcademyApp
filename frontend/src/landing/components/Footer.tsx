import { Link } from 'react-router-dom';

const footerColumns = [
  {
    title: 'Producto',
    links: [
      { label: 'Funcionalidades', href: '#features' },
      { label: 'Precios', href: '#' },
      { label: 'Demo', href: '#cta' },
      { label: 'FAQ', href: '#faq' },
    ],
  },
  {
    title: 'Recursos',
    links: [
      { label: 'Blog', href: '#' },
      { label: 'Documentación', href: '#' },
      { label: 'Centro de ayuda', href: '#' },
      { label: 'Estado del servicio', href: '#' },
    ],
  },
  {
    title: 'Legal',
    links: [
      { label: 'Términos de servicio', href: '#' },
      { label: 'Política de privacidad', href: '#' },
      { label: 'Cookies', href: '#' },
    ],
  },
];

export function Footer() {
  return (
    <footer className="bg-surface/80 backdrop-blur-lg border-t border-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 lg:py-16">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
          <div className="col-span-2 md:col-span-1">
            <Link
              to="/"
              className="text-lg font-bold text-text flex items-center gap-1.5"
            >
              <span className="w-7 h-7 rounded-md bg-gradient-to-br from-primary to-secondary flex items-center justify-center text-white text-xs font-bold">
                A
              </span>
              <span>
                Academia <span className="text-primary">SaaS</span>
              </span>
            </Link>
            <p className="mt-3 text-sm text-muted leading-relaxed max-w-xs">
              La plataforma integral de gestión educativa que transforma datos en
              decisiones. Moderniza tu institución con herramientas inteligentes.
            </p>
          </div>
          {footerColumns.map((col) => (
            <div key={col.title}>
              <h3 className="text-sm font-semibold text-text mb-3">
                {col.title}
              </h3>
              <ul className="space-y-2">
                {col.links.map((link) => (
                  <li key={link.label}>
                    {link.href.startsWith('#') ? (
                      <button
                        onClick={() => {
                          const id = link.href.replace('#', '');
                          const el = document.getElementById(id);
                          if (el) el.scrollIntoView({ behavior: 'smooth' });
                        }}
                        className="text-sm text-muted hover:text-text transition-colors"
                      >
                        {link.label}
                      </button>
                    ) : (
                      <a
                        href={link.href}
                        className="text-sm text-muted hover:text-text transition-colors"
                      >
                        {link.label}
                      </a>
                    )}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
        <div className="mt-10 pt-6 border-t border-border flex flex-col sm:flex-row items-center justify-between gap-4">
          <p className="text-xs text-muted">
            &copy; {new Date().getFullYear()} Academia SaaS. Todos los derechos
            reservados.
          </p>
          <div className="flex gap-4">
            <a href="#" className="text-muted hover:text-text transition-colors" aria-label="LinkedIn">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
                <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433a2.062 2.062 0 01-2.063-2.065 2.064 2.064 0 112.063 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z" />
              </svg>
            </a>
            <a href="#" className="text-muted hover:text-text transition-colors" aria-label="Twitter/X">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
                <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z" />
              </svg>
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
