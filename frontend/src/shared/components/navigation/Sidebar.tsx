import { NavLink } from 'react-router-dom';
import { useAuthStore, getDashboardPath } from '@/shared/store/useAuthStore';
import type { Role } from '@/shared/types/api.types';
import { DeleteButton } from '@/components/ui/delete-button';
import { LayoutDashboard, Users, GraduationCap, BookOpen, Building2, BarChart3, LogOut } from 'lucide-react';

const navIcons: Record<string, React.ElementType> = {
  Dashboard: LayoutDashboard,
  Reports: BarChart3,
  Teachers: Users,
  Students: GraduationCap,
  Courses: BookOpen,
  Classrooms: Building2,
  'My Courses': BookOpen,
  'My Grades': BarChart3,
  'My Schedule': LayoutDashboard,
  'My Attendance': BarChart3,
  'Grade Mgmt': BarChart3,
  Enrollments: Users,
  Attendance: BarChart3,
};

interface NavItem {
  label: string;
  path: string;
  roles: Role[];
}

const navItems: NavItem[] = [
  { label: 'Reports', path: '/app/director/reports', roles: ['DIRECTOR'] },
  { label: 'Teachers', path: '/app/director/teachers', roles: ['DIRECTOR'] },
  { label: 'Students', path: '/app/director/students', roles: ['DIRECTOR'] },
  { label: 'Courses', path: '/app/director/courses', roles: ['DIRECTOR'] },
  { label: 'Classrooms', path: '/app/director/classrooms', roles: ['DIRECTOR'] },
  { label: 'Enrollments', path: '/app/secretary/enrollments', roles: ['SECRETARY'] },
  { label: 'Attendance', path: '/app/secretary/attendance', roles: ['SECRETARY'] },
  { label: 'My Courses', path: '/app/teacher/courses', roles: ['TEACHER'] },
  { label: 'Grade Mgmt', path: '/app/teacher/grades', roles: ['TEACHER'] },
  { label: 'Attendance', path: '/app/teacher/attendance', roles: ['TEACHER'] },
  { label: 'My Courses', path: '/app/student/courses', roles: ['STUDENT'] },
  { label: 'My Grades', path: '/app/student/grades', roles: ['STUDENT'] },
  { label: 'My Schedule', path: '/app/student/schedule', roles: ['STUDENT'] },
  { label: 'My Attendance', path: '/app/student/attendance', roles: ['STUDENT'] },
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
              ? 'bg-[#1e3a5f] text-white font-medium border-l-[3px] border-[#3b82f6] rounded-l-none'
              : 'text-[#94a3b8] hover:text-[#f1f5f9] hover:bg-[#1a2744]'
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
      className={`fixed left-0 top-0 h-full bg-[#111827] border-r border-[#1e3048] transition-all duration-200 z-30 ${
        collapsed ? 'w-16' : 'w-64'
      }`}
    >
      <div className="flex items-center justify-between p-4 border-b border-[#1e3048]">
        {!collapsed && (
          <span className="font-semibold text-[#f1f5f9]">Academic SaaS</span>
        )}
        <button
          onClick={onToggle}
          className="p-1.5 rounded-md hover:bg-[#1a2744] text-[#94a3b8] transition-colors"
          aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          {collapsed ? '→' : '←'}
        </button>
      </div>

      <nav className="p-2 space-y-1">
        {!userRoles.includes('DIRECTOR') && renderNavLink('Dashboard', dashboardPath)}
        {visibleItems.map((item) => renderNavLink(item.label, item.path))}
      </nav>

      <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-[#1e3048]">
        {!collapsed && user && (
          <div className="flex items-center gap-3 mb-3">
            <div
              className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-xs font-bold"
              style={{ backgroundColor: '#3b82f620', color: '#3b82f6' }}
            >
              {getInitials(user.fullName)}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-sm font-medium text-[#f1f5f9] truncate">{user.fullName}</p>
              <p className="text-xs text-[#94a3b8] truncate">{user.roles.join(', ')}</p>
            </div>
          </div>
        )}
        <div className="mt-2">
          {collapsed ? (
            <button
              onClick={logout}
              className="flex items-center justify-center w-full p-2 rounded-md text-[#94a3b8] hover:text-[#ef4444] hover:bg-[#ef4444]/10 transition-colors"
              title="Sign Out"
            >
              <LogOut className="h-4 w-4" />
            </button>
          ) : (
            <DeleteButton
              onDelete={logout}
              deleteText="Sign Out"
              cancelText="Cancel"
              countdownSeconds={3}
            />
          )}
        </div>
      </div>
    </aside>
  );
}
