## [v1.0.0] - 2026-04-08
### Added
- Autenticación JWT con cookie HttpOnly (login, logout, me).
- Gestión de usuarios con roles (USER, ADMIN).
- CRUD completo de productos, categorías y packs (frontend y backend).
- Módulo de ventas con carrito interactivo y escáner de códigos de barras (cámara web/móvil).
- Reportes: inventario (con alerta de stock bajo) y ventas (con gráficas por día/mes).
- Exportación de reportes a Excel y PDF.
- Dashboard con estadísticas clave (productos, ventas, stock bajo).
- Diseño responsive con Tailwind CSS.
- Despliegue en la nube: backend en Render, frontend en Vercel.
- Documentación actualizada en README.

### Changed
- Migración de Basic Auth a JWT con cookie para mayor seguridad.
- Frontend migrado de HTML estático a Next.js (App Router).
- Mejora del manejo de errores y mensajes al usuario.
- Configuración CORS para entornos cross-origin (Vercel ↔ Render).

### Fixed
- Corrección de problemas de expiración de sesión (ahora configurable).
- Ajustes de permisos de cámara en el escáner.
- Solución de errores de CORS y redirecciones.

[Unreleased]: https://github.com/guadalupe182/pos-lite/compare/v1.0.0...HEAD
[v1.0.0]: https://github.com/guadalupe182/pos-lite/releases/tag/v1.0.0