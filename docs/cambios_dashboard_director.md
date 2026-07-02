Eres un UI/UX engineer senior. Tienes un dashboard Academic SaaS construido con 
React + TypeScript + Tailwind CSS. Necesito que refactorices el diseño visual 
completo manteniendo la funcionalidad existente.

## STACK
- React 18 + TypeScript
- Tailwind CSS v3
- Framer Motion (disponible)
- Recharts (disponible para gráficas)
- Lucide React (iconos)

## PROBLEMAS A RESOLVER

### 1. KPI Cards (Dashboard + Reports)
ACTUAL: 5 cards idénticas con número grande y label. Sin contexto ni jerarquía.
CAMBIAR A:
- Añadir icono representativo por métrica (Users, GraduationCap, BookOpen, TrendingUp, Calendar)
- Añadir indicador de tendencia: flecha ↑↓ con porcentaje vs período anterior
- Color semántico SOLO en Avg Score y Attendance Rate:
  * ≥ 80% → accent verde (#22c55e)
  * 60–79% → accent amarillo (#eab308)  
  * < 60% → accent rojo (#ef4444)
- Layout: icono arriba-derecha, valor grande, label pequeño, tendencia abajo

### 2. Course Performance Bars
ACTUAL: Todas las barras son amarillas sin distinción. 0% se ve igual que 80%.
CAMBIAR A:
- Color semántico por valor:
  * ≥ 80% → #22c55e (verde)
  * 60–79% → #eab308 (amarillo)  
  * 40–59% → #f97316 (naranja)
  * < 40% → #ef4444 (rojo)
  * 0% sin datos → #374151 (gris, con label "Sin datos")
- Mostrar porcentaje alineado a la derecha con el mismo color de la barra
- Animación de entrada con Framer Motion (width de 0 a valor real, delay escalonado)

### 3. At-Risk Students Panel
ACTUAL: Lista plana con mismo peso visual que el resto. Badge de porcentaje en rojo genérico.
CAMBIAR A:
- Header: borde izquierdo rojo de 3px en el card contenedor
- Badge de conteo ("12 students") con color rojo y dot animado (pulse)
- Cada student card:
  * Borde izquierdo cuyo color varía por severidad:
    - < 55% → rojo (#ef4444)
    - 55–60% → naranja (#f97316)
    - > 60% → amarillo (#eab308)
  * Avatar con iniciales del estudiante (2 letras, color de fondo según severidad)
  * Nombre en bold, descripción en texto secundario más pequeño
  * Badge de porcentaje alineado a la derecha con color semántico
- Ordenar por promedio ascendente (el más en riesgo primero)

### 4. Tablas de Gestión (Teachers, Students, Courses, Classrooms)
ACTUAL: Tablas planas con rows idénticos. Status badge "ACTIVE" en cada fila.
CAMBIAR A:
- Row hover: background sutil (#1e2a3a) con transición 150ms
- Status badge: solo mostrarlo si es INACTIVE o tiene estado especial. 
  Si todos son ACTIVE, reemplazar la columna Status por una métrica útil:
  * Teachers → "N secciones activas"
  * Students → mini-barra de progreso de su promedio
  * Courses → conteo de enrolled students
  * Classrooms → "capacidad usada / total"
- Actions: reemplazar texto plano "Edit / Delete" por icon buttons con tooltip:
  * Pencil icon → Edit (hover: azul)
  * Trash icon → Delete (hover: rojo)
  * Para Students/Teachers: añadir Eye icon → Ver perfil (hover: gris)
- Search input: añadir icono Search de Lucide dentro del input a la izquierda
- Añadir skeleton loader para el estado de carga inicial de cada tabla

### 5. Sidebar Navigation
ACTUAL: Punto azul como único indicador de página activa. Sin feedback visual claro.
CAMBIAR A:
- Item activo: background #1e3a5f, borde izquierdo 3px azul (#3b82f6), 
  texto blanco, icono del color del borde
- Item hover: background #1a2744 con transición 150ms
- Añadir icono a cada item de navegación (Lucide):
  * Dashboard → LayoutDashboard
  * Teachers → Users
  * Students → GraduationCap
  * Courses → BookOpen
  * Classrooms → Building2
  * Reports → BarChart3
- Añadir tooltip con el nombre cuando el sidebar está colapsado
- Bottom section (usuario): separador visible, avatar con iniciales, 
  nombre + rol en dos líneas, Sign Out con icono LogOut

### 6. Reports Page
ACTUAL: Duplica exactamente el Dashboard. No agrega valor propio.
CAMBIAR A:
- Mantener las KPI cards y los paneles del dashboard
- Añadir debajo de Course Performance una sección "Grade Distribution" 
  con un BarChart de Recharts (horizontal, colores semánticos)
- El Attendance Trend chart (ya existe pero vacío): 
  * Implementar con LineChart de Recharts
  * Línea principal azul (#3b82f6), área bajo la curva con opacidad 20%
  * Grid lines sutiles, tooltips custom con el mismo dark theme
- Date Range filter: aplicarlo realmente como estado local que filtra 
  los datos mostrados (mock filtering por ahora está bien)

### 7. Empty States
ACTUAL: Sin manejo de estados vacíos.
AÑADIR:
- Cuando una tabla no tiene resultados de búsqueda:
  * Icono grande centrado (SearchX de Lucide)
  * Texto: "No se encontraron resultados para '[término]'"
  * Botón secundario: "Limpiar búsqueda"
- Cuando no hay datos en una sección:
  * Icono representativo de la sección
  * CTA para agregar el primer item

## DESIGN TOKENS A USAR (mantener consistencia)

colors:

bg-primary: #0f172a      (fondo principal)

bg-card: #1a2332         (cards)

bg-card-hover: #1e2a3a   (hover en rows/cards)

bg-sidebar: #111827      (sidebar)

border: #1e3048          (borders)

text-primary: #f1f5f9    (texto principal)

text-secondary: #94a3b8  (texto secundario)

accent-blue: #3b82f6     (primary action)

accent-green: #22c55e    (éxito / bueno)

accent-yellow: #eab308   (advertencia / medio)

accent-orange: #f97316   (alerta)

accent-red: #ef4444      (crítico / peligro)

## INSTRUCCIONES DE IMPLEMENTACIÓN
1. Refactorizar componente por componente, empezando por los compartidos 
   (KPICard, PerformanceBar, StudentRiskCard, DataTable, Sidebar)
2. Extraer componentes reutilizables a /components/ui/ si no existen
3. No romper la lógica de datos existente, solo cambiar presentación
4. Todos los cambios deben ser responsive (mobile-first)
5. Usar Framer Motion solo donde agregue valor real: 
   entrada de cards, barras de progreso, hover states complejos
6. Mantener accesibilidad: aria-labels en icon buttons, 
   contraste mínimo WCAG AA