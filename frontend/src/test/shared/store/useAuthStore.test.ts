import { useAuthStore } from '@/shared/store/useAuthStore';

const mockUser = {
  userId: '1',
  email: 'admin@academy.edu',
  fullName: 'Admin User',
  roles: ['DIRECTOR'],
};

beforeEach(() => {
  useAuthStore.setState({ user: null, isAuthenticated: false });
  localStorage.clear();
});

describe('useAuthStore', () => {
  it('starts with no user and isAuthenticated=false', () => {
    const state = useAuthStore.getState();

    expect(state.user).toBeNull();
    expect(state.isAuthenticated).toBe(false);
  });

  it('after login, user is set and isAuthenticated becomes true', () => {
    useAuthStore.getState().login(mockUser, 'access-token', 'refresh-token');

    const state = useAuthStore.getState();
    expect(state.user).toEqual(mockUser);
    expect(state.isAuthenticated).toBe(true);
  });

  it('after logout, user is cleared and isAuthenticated becomes false', () => {
    useAuthStore.getState().login(mockUser, 'access-token', 'refresh-token');
    useAuthStore.getState().logout();

    const state = useAuthStore.getState();
    expect(state.user).toBeNull();
    expect(state.isAuthenticated).toBe(false);
  });

  it('login() stores refreshToken in localStorage', () => {
    useAuthStore.getState().login(mockUser, 'access-token', 'my-refresh-token');

    expect(localStorage.getItem('refreshToken')).toBe('my-refresh-token');
  });

  it('logout() clears refreshToken from localStorage', () => {
    useAuthStore.getState().login(mockUser, 'access-token', 'refresh-token');
    useAuthStore.getState().logout();

    expect(localStorage.getItem('refreshToken')).toBeNull();
  });
});
