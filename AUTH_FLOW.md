# Flujo de autenticación (JWT por cookie HttpOnly)

## Flujo completo
1. `POST /api/v1/auth/login` con credenciales.
2. Backend emite JWT y lo setea en cookie `access_token` (HttpOnly).
3. Peticiones subsiguientes envían automáticamente la cookie (misma origin o CORS con credenciales).
4. `SecurityConfig` resuelve el token desde cookie o header `Authorization: Bearer`.
5. `POST /api/v1/auth/logout` borra la cookie.

## Pruebas en local
- Frontend en `http://localhost:3000`/`3001`, backend `http://localhost:8081`.
- Cookie en dev: `Secure=false`, `SameSite=Lax` (navegadores rechazan Secure en HTTP).
- Verificar en DevTools → Application → Cookies.

## Pruebas en producción
- HTTPS obligatorio; cookie `Secure=true`, `SameSite=None`.
- Proxy/load balancer debe propagar `X-Forwarded-Proto` (configurar `server.forward-headers-strategy=framework`).
- Verificar logout borra la cookie.