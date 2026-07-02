export const navLinks = [
  { label: 'Funcionalidades', href: '#features' },
  { label: 'Beneficios', href: '#benefits' },
  { label: 'FAQ', href: '#faq' },
];

export const heroContent = {
  title: 'Gestiona tu institución educativa con datos en tiempo real',
  subtitle:
    'Centraliza notas, asistencias y comunicaciones en un solo lugar. Olvídate del caos de Excel y toma decisiones informadas al instante.',
  ctaPrimary: { text: 'Solicitar Demo', href: '#cta' },
  ctaSecondary: { text: 'Ver Funcionalidades', href: '#features' },
};

export interface Benefit {
  icon: string;
  title: string;
  description: string;
}

export const benefits: Benefit[] = [
  {
    icon: '📊',
    title: 'Dashboard en tiempo real',
    description:
      'Visualiza indicadores clave al instante. Toma decisiones con datos actualizados, no con informes de la semana pasada.',
  },
  {
    icon: '⚠️',
    title: 'Detección de alumnos en riesgo',
    description:
      'Identifica patrones de bajo rendimiento y ausentismo antes de que sea tarde. Interviene a tiempo con alertas inteligentes.',
  },
  {
    icon: '📋',
    title: 'Gestión unificada de notas',
    description:
      'Consolida toda la información académica en un solo sistema. Olvida las planillas dispersas y los errores de transcripción.',
  },
  {
    icon: '💬',
    title: 'Comunicación multi-rol',
    description:
      'Conecta directivos, docentes, estudiantes y familias con mensajería integrada. Comunicaciones claras y sin intermediarios.',
  },
  {
    icon: '📈',
    title: 'Reportes inteligentes',
    description:
      'Genera informes automáticos con visualizaciones claras. Exporta a PDF o Excel con un solo clic.',
  },
  {
    icon: '✏️',
    title: 'Matrícula simplificada',
    description:
      'Digitaliza el proceso de inscripción con formularios inteligentes. Reduce la carga administrativa y los errores de registro.',
  },
];

export interface Feature {
  title: string;
  description: string;
  items: string[];
  side: 'left' | 'right';
  color: string;
}

export const features: Feature[] = [
  {
    title: 'Panel de control que anticipa el futuro',
    description:
      'Nuestro dashboard no solo muestra lo que pasó: te alerta sobre lo que está por pasar. Con inteligencia integrada, detecta tendencias y patrones antes de que afecten el rendimiento.',
    items: [
      'Alertas tempranas de alumnos en riesgo',
      'Tendencias de rendimiento por curso',
      'Indicadores de asistencia en vivo',
    ],
    side: 'left',
    color: 'from-indigo-500 to-indigo-600',
  },
  {
    title: 'Planilla de notas inteligente',
    description:
      'Carga notas desde Excel o ingrésalas manualmente. El sistema calcula promedios, genera curvas de rendimiento y detecta anomalías automáticamente.',
    items: [
      'Importación masiva desde Excel',
      'Cálculo automático de promedios y curvas',
      'Detección de anomalías en calificaciones',
    ],
    side: 'right',
    color: 'from-cyan-500 to-cyan-600',
  },
  {
    title: 'Comunicaciones sin fricción',
    description:
      'Mensajería integrada para cada rol de tu institución. Envía comunicados, recibe notificaciones y mantén a todos informados sin depender de cadenas de WhatsApp.',
    items: [
      'Notificaciones por rol y curso',
      'Comunicados masivos con confirmación de lectura',
      'Bandeja de mensajes unificada',
    ],
    side: 'left',
    color: 'from-violet-500 to-violet-600',
  },
];

export interface Testimonial {
  institution: string;
  quote: string;
}

export const testimonials: Testimonial[] = [
  {
    institution: 'Colegio San José',
    quote: 'Espacio reservado para el testimonio de nuestros primeros clientes. ¿Te gustaría ser uno de ellos?',
  },
  {
    institution: 'Instituto Técnico Albert Einstein',
    quote: 'Pronto compartiremos cómo Academia SaaS transformó su gestión educativa.',
  },
  {
    institution: 'Escuela de Negocios del Pacífico',
    quote: 'Tu testimonio podría estar aquí. Contáctanos para ser parte de nuestra red de instituciones innovadoras.',
  },
];

export interface FAQ {
  question: string;
  answer: string;
}

export const faqs: FAQ[] = [
  {
    question: '¿Necesito instalar software?',
    answer:
      'No. Academia SaaS es 100% online y funciona en cualquier navegador moderno. No necesitas instalar nada en tus computadoras ni servidores.',
  },
  {
    question: '¿Cuánto tiempo toma implementar la plataforma?',
    answer:
      'La implementación inicial toma entre 1 y 3 días hábiles. Nuestro equipo te acompaña en todo el proceso, desde la migración de datos hasta la capacitación del personal.',
  },
  {
    question: '¿Puedo migrar datos de Excel?',
    answer:
      'Sí. Contamos con herramientas de importación que reconocen la estructura de tus planillas. Solo subes tus archivos y el sistema organiza la información automáticamente.',
  },
  {
    question: '¿Ofrecen capacitación para el personal?',
    answer:
      'Sí. Incluimos sesiones de capacitación virtual para todos los roles de tu institución: directivos, secretarios, docentes y administrativos.',
  },
  {
    question: '¿Qué requisitos técnicos necesito?',
    answer:
      'Solo necesitas una computadora o tablet con conexión a internet y un navegador actualizado (Chrome, Firefox, Edge o Safari).',
  },
];

export const mockupData = {
  stats: [
    { label: 'Total Alumnos', value: 1247, suffix: '', prefix: '' },
    { label: 'Profesores', value: 48, suffix: '', prefix: '' },
    { label: 'Cursos Activos', value: 32, suffix: '', prefix: '' },
    { label: 'Asistencia Prom.', value: 94, suffix: '%', prefix: '' },
  ],
  atRiskStudents: [
    { name: 'María García', average: 68, status: 'critical' as const },
    { name: 'Juan Pérez', average: 72, status: 'warning' as const },
    { name: 'Ana López', average: 65, status: 'critical' as const },
  ],
  coursePerformance: [
    { course: 'Matemáticas', value: 85, color: 'bg-primary' },
    { course: 'Lenguaje', value: 72, color: 'bg-accent' },
    { course: 'Ciencias', value: 78, color: 'bg-secondary' },
    { course: 'Historia', value: 91, color: 'bg-success' },
    { course: 'Inglés', value: 68, color: 'bg-warning' },
  ],
};
