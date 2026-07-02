import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import { LoginForm } from '@/features/auth/components/LoginForm';

const server = setupServer(
  http.post('/api/v1/auth/login', () =>
    HttpResponse.json(
      {
        accessToken: 'mock-token',
        refreshToken: 'mock-refresh',
        userId: '1',
        email: 'admin@academy.edu',
        fullName: 'Admin User',
        roles: ['DIRECTOR'],
      },
      { status: 200 },
    ),
  ),
  http.post('/api/v1/auth/refresh', () =>
    HttpResponse.json({ accessToken: 'refreshed-token' }, { status: 200 }),
  ),
);

beforeAll(() => server.listen({ onUnhandledRequest: 'bypass' }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

function renderComponent() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <LoginForm />
      </BrowserRouter>
    </QueryClientProvider>,
  );
}

describe('LoginForm', () => {
  it('renders email and password inputs', () => {
    renderComponent();

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  });

  it('shows validation errors for empty fields', async () => {
    const user = userEvent.setup();
    renderComponent();

    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(screen.getByText(/email is required/i)).toBeInTheDocument();
      expect(screen.getByText(/password is required/i)).toBeInTheDocument();
    });
  });

  it('calls mutation with correct data on submit', async () => {
    let capturedData: unknown = null;

    server.use(
      http.post('/api/v1/auth/login', async ({ request }) => {
        capturedData = await request.json();
        return HttpResponse.json(
          {
            accessToken: 'mock-token',
            refreshToken: 'mock-refresh',
            userId: '1',
            email: 'admin@academy.edu',
            fullName: 'Admin User',
            roles: ['DIRECTOR'],
          },
          { status: 200 },
        );
      }),
    );

    const user = userEvent.setup();
    renderComponent();

    await user.type(screen.getByLabelText(/email/i), 'admin@academy.edu');
    await user.type(screen.getByLabelText(/password/i), 'password123');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(capturedData).toEqual({
        email: 'admin@academy.edu',
        password: 'password123',
      });
    });
  });

  it('shows error message on failed login', async () => {
    server.use(
      http.post('/api/v1/auth/login', () =>
        HttpResponse.json(
          { errorCode: 'INVALID_CREDENTIALS', message: 'Invalid email or password' },
          { status: 401 },
        ),
      ),
    );

    const user = userEvent.setup();
    renderComponent();

    await user.type(screen.getByLabelText(/email/i), 'wrong@email.com');
    await user.type(screen.getByLabelText(/password/i), 'wrongpass');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent(/401/i);
    });
  });
});
