import React from 'react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { router } from './app/routes/router';
import { ThemeProvider } from './shared/hooks/useTheme';
import { QueryProvider } from './app/providers/QueryProvider';
import './shared/styles/tokens.css';
import './shared/styles/globals.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ThemeProvider>
      <QueryProvider>
        <RouterProvider router={router} />
      </QueryProvider>
    </ThemeProvider>
  </React.StrictMode>,
);
