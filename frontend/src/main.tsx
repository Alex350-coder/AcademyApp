import React from 'react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { router } from './app/routes/router';
import { QueryProvider } from './app/providers/QueryProvider';
import { ToastContainer } from './shared/components/feedback/Toast';
import './shared/styles/tokens.css';
import './shared/styles/globals.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryProvider>
      <RouterProvider router={router} />
      <ToastContainer />
    </QueryProvider>
  </React.StrictMode>,
);
