import { test, expect } from '@playwright/test';
import { LoginPage } from './pages/LoginPage';

test.describe('Grading Flow', () => {
  test.skip('teacher can record grades', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    await loginPage.login('teacher@academy.edu', 'password123');

    await page.goto('/app/teacher/grades');
    await expect(page.getByRole('heading', { name: /grade/i })).toBeVisible();

    await page.getByLabel(/select.*section/i).selectOption('section-1');

    await page.getByPlaceholder(/grade/i).first().fill('85');
    await page.getByPlaceholder(/grade/i).last().fill('92');

    await page.getByRole('button', { name: /save/i }).click();
    await expect(page.getByText(/grades saved/i)).toBeVisible();
  });

  test.skip('student can view their grades', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();

    await loginPage.login('student@academy.edu', 'password123');

    await page.goto('/app/student/grades');
    await expect(page.getByRole('heading', { name: /my grades|mis calificaciones/i })).toBeVisible();
  });
});
