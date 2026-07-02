import { test, expect } from '@playwright/test';
import { LoginPage } from './pages/LoginPage';

test.describe('Authentication', () => {
  test('visitor can see landing page', async ({ page }) => {
    await page.goto('/');

    await expect(page.getByText(/academia.*saas/i)).toBeVisible();
    await expect(page.getByRole('button', { name: /iniciar sesión/i })).toBeVisible();
  });

  test('login form validates required fields', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    await loginPage.submit();

    await expect(page.getByText(/email is required/i)).toBeVisible();
    await expect(page.getByText(/password is required/i)).toBeVisible();
  });

  test('user with valid credentials can login', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    await loginPage.login('admin@academy.edu', 'password123');

    await expect(page).toHaveURL(/\/app\/dashboard/, { timeout: 10000 });
  });

  test('unauthenticated user is redirected to login', async ({ page }) => {
    await page.goto('/app/director');

    await expect(page).toHaveURL(/\/login/);
  });

  test('login as director can access director area', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    await loginPage.login('admin@academy.edu', 'password123');

    await page.goto('/app/director');
    await expect(page.getByRole('heading', { name: /dashboard/i })).toBeVisible({ timeout: 10000 });
  });
});
