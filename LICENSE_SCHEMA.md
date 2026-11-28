# Esquema extendido de License (propuesta)

## Campos actuales
- `id`, `license_key`, `tenant_id`, `expiration_date`, `status`, `features`

## Campos sugeridos (no implementados)
- `plan` (FREE/PRO/ENTERPRISE)
- `max_users` / `max_devices`
- `modules` (si se separa de `features`)
- `issued_at` / `activated_at`
- `customer_email` (auditoría)

## Impacto en endpoints
- `AuthService.loginAndIssueToken`: incluir `plan`, `modules` en claims si se agregan.
- `SecurityConfig`: convertir nuevos claims a authorities si aplica.
- Validación de licencia: el CRM debería devolver estos campos; `LicenseValidationResponse` debe ampliarse.

## Migración
```sql
ALTER TABLE license ADD COLUMN plan VARCHAR(30);
ALTER TABLE license ADD COLUMN max_users INT;
```