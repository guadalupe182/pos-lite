# Auditoría de endpoints — pos-lite

> Nota: el frontend no está en este repositorio. El consumo se marca como "por confirmar" hasta cruzar con el front.

## Endpoints por módulo (estado actual)

| Módulo | Ruta base | Auth/Rol | Tenant | Uso front | Recomendación |
|---|---|---|---|---|---|
| Auth | `/api/v1/auth` | público (login/register) | n/a | sí | Mantener |
| Product | `/api/v1/products` | `ADMIN/MANAGER` mutaciones; lectura amplia | single-tenant | por confirmar | Mantener; revisar si se usa adjust-batch |
| Category | `/api/v1/categories` | `ADMIN/MANAGER` mutaciones | single-tenant | por confirmar | Mantener |
| Sale | `/api/v1/sales` | `ADMIN/MANAGER` reportes | single-tenant | sí | Mantener |
| Cash | `/api/v1/cash` | roles + MULTI_CASH | single-tenant | sí | Mantener |
| Branch | `/api/v1/branches` | `ADMIN/MANAGER` mutaciones | single-tenant | por confirmar | Mantener |
| Pack | `/api/v1/packs` | `ADMIN/MANAGER` create | single-tenant | por confirmar | Candidato a legacy si no se usa |
| Notification | `/api/v1/notifications` | authenticated + owner | por usuario | sí | Mantener |
| Restock | `/api/v1/restock` | `ADMIN/MANAGER` | single-tenant | por confirmar | Mantener (aguacates) |
| Payment | `/api/v1/payments` | authenticated | n/a | sí | Mantener; validar precios server-side |
| Loyalty | `/api/v1/loyalty` | `LOYALTY` | por email | sí | Mantener |
| Fit | `/api/v1/memberships` | `FIT` | tenant_id | sí | Mantener |
| Style | `/api/v1/boutique-items` | `STYLE` | tenant_id | sí | Mantener |

## Endpoints sin consumo confirmado
- `PackController` (`/sell`, `/create`, `/{barcode}`): revisar si el front lo usa; si no, marcar legacy y evaluar eliminación.
- `BranchController`: CRUD completo; confirmar si el front administra sucursales.
- `CategoryController`: confirmar uso de create/delete desde front.

## Acción sugerida
1. Cruzar esta tabla con el frontend (rutas y llamadas HTTP).
2. Eliminar o deprecar endpoints sin uso real.
3. Documentar en Swagger los endpoints deprecated.