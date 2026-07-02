import { Outlet } from 'react-router-dom';

function App() {
  return (
    <div className="min-h-screen bg-background text-text">
      <Outlet />
    </div>
  );
}

export default App;
