# Design System — Academic SaaS

## Tokens de Color

| Token | Light | Dark | Uso |
|-------|-------|------|-----|
| `--color-primary` | #2563EB | #3B82F6 | Acciones principales, enlaces, botones primarios |
| `--color-secondary` | #4F46E5 | #6366F1 | Acciones secundarias |
| `--color-accent` | #06B6D4 | #22D3EE | Highlights, badges informativos |
| `--color-background` | #F8FAFC | #020617 | Fondo de página |
| `--color-surface` | #FFFFFF | #0F172A | Tarjetas, modales, inputs |
| `--color-text` | #0F172A | #F8FAFC | Texto principal |
| `--color-muted` | #64748B | #94A3B8 | Texto secundario, placeholders |
| `--color-success` | #22C55E | #4ADE80 | Operaciones exitosas |
| `--color-warning` | #F59E0B | #FBBF24 | Alertas |
| `--color-danger` | #EF4444 | #F87171 | Errores, acciones destructivas — solo para acciones irreversibles |
| `--color-border` | #E2E8F0 | #1E293B | Bordes de componentes |

## Tipografía

- **UI**: Inter (sistema)
- **Escala**: 12/14/16/18/24/32/48px
- **Pesos**: Regular 400, Medium 500, Semibold 600, Bold 700

## Espaciado

Basado en 4px: 4/8/12/16/20/24/32/40/48/64px

## Radios

- `--radius-sm`: 6px (inputs, badges)
- `--radius-md`: 10px (buttons, cards)
- `--radius-lg`: 16px (modals, drawers)
- `--radius-full`: 9999px (pills, avatars)

## Sombras

- `elevation-1`: cards en estado normal
- `elevation-2`: dropdowns, modales
- `elevation-3`: drawers, toasts

## Componentes

### Button
- Variants: primary | secondary | ghost | danger
- Sizes: sm | md | lg
- Estados: hover, focus, active, disabled, loading

### Input
- Estados: normal, focus, error, disabled
- Label, helper text y mensaje de error

### Badge
- Variants: default | success | warning | danger | info

### Card
- Padding: sm | md | lg
- Subcomponentes: CardHeader, CardTitle, CardContent
