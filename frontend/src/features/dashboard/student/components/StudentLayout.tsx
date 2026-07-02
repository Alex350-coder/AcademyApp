import { Outlet } from 'react-router-dom';
import { Sidebar } from '@/shared/components/navigation/Sidebar';

export function StudentLayout() {
  return (
    <div className="flex min-h-screen bg-background">
      <Sidebar />
      <div className="flex-1 ml-64 p-6">
        <Outlet />
      </div>
    </div>
  );
}
