# Diseño de roles (pendiente de implementación)

## Objetivo
Soportar `ADMIN`, `MANAGER`, `CASHIER`, `USER` sin romper el flujo actual.

## Cambios necesarios
1. **Enum `Role`**: ya incluye `MANAGER` y `CASHIER`.
2. **Entidad `User`**: sin cambios estructurales; roles siguen en `app_user_roles`.
3. **Registro/invitaciones**: agregar códigos de invitación por rol (config `admin.invite.code`, `manager.invite.code`, etc.) o endpoint de gestión de usuarios (ADMIN).
4. **Seguridad**:
   - `@PreAuthorize` por método usando `hasAnyRole(...)`.
   - Endpoints admin-only: `hasRole('ADMIN')`.
   - Operativos: `hasAnyRole('ADMIN','MANAGER','CASHIER','USER')`.
5. **Frontend**: ocultar opciones según rol/feature.

## Nota
No implementado todavía; solo diseño. Requiere migración de datos si se asignan roles nuevos a usuarios existentes.