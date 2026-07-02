import { test, expect } from '@playwright/test';
import { LoginPage } from './pages/LoginPage';

test.describe('Enrollment Flow', () => {
  test.skip('secretary can complete enrollment wizard', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    await loginPage.login('secretary@academy.edu', 'password123');

    await page.goto('/app/secretary/enrollments');
    await expect(page.getByRole('heading', { name: /enrollment/i })).toBeVisible();

    await page.getByPlaceholder(/search.*student/i).fill('Juan Pérez');
    await page.getByRole('button', { name: /search/i }).click();

    await expect(page.getByText('Juan Pérez')).toBeVisible();

    await page.getByRole('button', { name: /select.*section/i }).click();

    await page.getByRole('button', { name: /confirm|enroll/i }).click();

    await expect(page.getByText(/enrollment created/i)).toBeVisible();
  });
});
