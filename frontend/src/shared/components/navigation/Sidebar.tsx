import { NavLink } from 'react-router-dom';
import { useAuthStore, getDashboardPath } from '@/shared/store/useAuthStore';
import type { Role } from '@/shared/types/api.types';
import { DeleteButton } from '@/components/ui/delete-button';
import { LayoutDashboard, Users, GraduationCap, BookOpen, Building2, BarChart3, LogOut } from 'lucide-react';

const navIcons: Record<string, React.ElementType> = {
  Panel: LayoutDashboard,
  Reportes: BarChart3,
  Docentes: Users,
  Estudiantes: GraduationCap,
  Cursos: BookOpen,
  Aulas: Building2,
  'Mis Cursos': BookOpen,
  'Mis Notas': BarChart3,
  'Mi Horario': LayoutDashboard,
  'Mi Asistencia': BarChart3,
  'Gestión de Notas': BarChart3,
  Matrículas: Users,
  Asistencia: BarChart3,
};

interface NavItem {
  label: string;
  path: string;
  roles: Role[];
}

const navItems: NavItem[] = [
  { label: 'Reportes', path: '/app/director/reports', roles: ['DIRECTOR'] },
  { label: 'Docentes', path: '/app/director/teachers', roles: ['DIRECTOR'] },
  { label: 'Estudiantes', path: '/app/director/students', roles: ['DIRECTOR'] },
  { label: 'Cursos', path: '/app/director/courses', roles: ['DIRECTOR'] },
  { label: 'Aulas', path: '/app/director/classrooms', roles: ['DIRECTOR'] },
  { label: 'Matrículas', path: '/app/secretary/enrollments', roles: ['SECRETARY'] },
  { label: 'Asistencia', path: '/app/secretary/attendance', roles: ['SECRETARY'] },
  { label: 'Mis Cursos', path: '/app/teacher/courses', roles: ['TEACHER'] },
  { label: 'Gestión de Notas', path: '/app/teacher/grades', roles: ['TEACHER'] },
  { label: 'Asistencia', path: '/app/teacher/attendance', roles: ['TEACHER'] },
  { label: 'Mis Cursos', path: '/app/student/courses', roles: ['STUDENT'] },
  { label: 'Mis Notas', path: '/app/student/grades', roles: ['STUDENT'] },
  { label: 'Mi Horario', path: '/app/student/schedule', roles: ['STUDENT'] },
  { label: 'Mi Asistencia', path: '/app/student/attendance', roles: ['STUDENT'] },
];

interface SidebarProps {
  collapsed?: boolean;
  onToggle?: () => void;
}

function getInitials(name: string): string {
  return name
    .split(' ')
    .map((w) => w[0])
    .filter(Boolean)
    .slice(0, 2)
    .join('')
    .toUpperCase();
}

export function Sidebar({ collapsed = false, onToggle }: SidebarProps) {
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);

  const userRoles = user?.roles ?? [];
  const dashboardPath = getDashboardPath(user);
  const visibleItems = navItems.filter(
    (item) => userRoles.some((r) => item.roles.includes(r as Role)),
  );

  function renderNavLink(label: string, path: string) {
    const Icon = navIcons[label];
    return (
      <NavLink
        to={path}
        className={({ isActive }) =>
          `flex items-center gap-3 px-3 py-2 rounded-md text-sm transition-all duration-150 ${
            isActive
              ? 'bg-white/15 text-white font-medium border-l-[3px] border-white rounded-l-none'
              : 'text-white/60 hover:text-white hover:bg-white/10'
          } ${collapsed ? 'justify-center' : ''}`
        }
        title={collapsed ? label : undefined}
      >
        {Icon && (
          <span className="flex-shrink-0 w-5 h-5 flex items-center justify-center">
            <Icon className="h-4 w-4" />
          </span>
        )}
        {!collapsed && <span>{label}</span>}
      </NavLink>
    );
  }

  return (
    <aside
      className={`fixed left-0 top-0 h-full bg-primary border-r border-white/10 transition-all duration-200 z-30 ${
        collapsed ? 'w-16' : 'w-64'
      }`}
    >
      <div className="flex items-center justify-between p-4 border-b border-white/10">
        {!collapsed && (
          <span className="font-semibold text-white">Academia SaaS</span>
        )}
        <button
          onClick={onToggle}
          className="p-1.5 rounded-md hover:bg-white/10 text-white/60 transition-colors"
          aria-label={collapsed ? 'Expandir barra lateral' : 'Contraer barra lateral'}
        >
          {collapsed ? '→' : '←'}
        </button>
      </div>

      <nav className="p-2 space-y-1">
        {!userRoles.includes('DIRECTOR') && renderNavLink('Panel', dashboardPath)}
        {visibleItems.map((item) => renderNavLink(item.label, item.path))}
      </nav>

      <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-white/10">
        {!collapsed && user && (
          <div className="flex items-center gap-3 mb-3">
            <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-white/15 text-white text-xs font-bold">
              {getInitials(user.fullName)}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-sm font-medium text-white truncate">{user.fullName}</p>
              <p className="text-xs text-white/60 truncate">{user.roles.join(', ')}</p>
            </div>
          </div>
        )}
        <div className="mt-2">
          {collapsed ? (
            <button
              onClick={logout}
              className="flex items-center justify-center w-full p-2 rounded-md text-white/60 hover:text-danger hover:bg-danger/10 transition-colors"
              title="Cerrar sesión"
            >
              <LogOut className="h-4 w-4" />
            </button>
          ) : (
            <DeleteButton
              onDelete={logout}
              deleteText="Cerrar sesión"
              cancelText="Cancelar"
              countdownSeconds={3}
            />
          )}
        </div>
      </div>
    </aside>
  );
}
