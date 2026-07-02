/**
 * Sanitize user input for safe HTML rendering.
 */
export function sanitizeHtml(input: string): string {
  if (!input) return '';

  return input
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#x27;');
}

/**
 * Sanitize user input for display in text content.
 */
export function sanitizeText(input: string): string {
  if (!input) return '';
  return input.trim();
}

/**
 * Sanitize a URL to prevent javascript: protocol injection.
 */
export function sanitizeUrl(url: string): string {
  if (!url) return '';
  const lower = url.toLowerCase().trim();
  if (lower.startsWith('javascript:') || lower.startsWith('data:text/html')) {
    return '';
  }
  return url;
}

/**
 * Strip all HTML tags from a string.
 */
export function stripHtml(input: string): string {
  if (!input) return '';
  return input.replace(/<[^>]*>/g, '');
}
