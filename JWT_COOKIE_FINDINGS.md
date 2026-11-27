# Hallazgos JWT / Cookie HttpOnly

## Problema observado
- La cookie `access_token` se creaba con `secure(true)` y `sameSite("None")`.
- En desarrollo (HTTP, localhost) los navegadores **rechazan** cookies `Secure`, por lo que no se guardaban ni se enviaban.
- El logout no incluía `sameSite`, por lo que no borraba correctamente la cookie cross-site.

## Corrección aplicada
- `secure` y `sameSite` se derivan del request:
  - HTTPS (prod): `secure=true`, `sameSite=None`
  - HTTP (dev): `secure=false`, `sameSite=Lax`
- Logout usa los mismos atributos.
- Se eliminó el token del body de `/login` para mantener el flujo exclusivo por cookie HttpOnly (SEC-06).

## Nota
- En producción detrás de proxy/load balancer, asegurar que `request.isSecure()` refleje HTTPS (configurar `server.forward-headers-strategy=framework` o headers X-Forwarded-Proto).