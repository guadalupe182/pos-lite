# AGENTS.md — POS Backend (Spring Boot)

## 🚨 REGLAS DE ORO DE GIT (FLUJO GITFLOW)
- **NUNCA** trabajes sobre `main`, `dev` o `sipovad` directamente.
- **SIEMPRE** crea una rama `feature/` o `fix/` **a partir de `dev`**.
- Al terminar, haz push a tu rama. El humano hará el PR hacia `dev`, se probará en Sandbox y luego se mergeará a `main`.
- **NO** hagas Merge ni PRs. Eso lo hace el humano.
- Si un ticket no requiere cambios reales, avísalo en lugar de crear ramas vacías.

## 🏗️ ARQUITECTURA Y ESTILO
- El proyecto usa Java 17, Spring Boot 3.4.x y Maven.
- Las rutas de los controladores deben tener el prefijo `/api/v1/`.
- Usa SLF4J para logs (nunca `System.out.println`).

## 🧪 Validación
- Corre `./mvnw clean test` antes de decir que terminaste.
- Si el cambio es de infraestructura o configuración, avísalo para hacer un build de Docker.

## 📦 Stack y comandos
- Java 17, Spring Boot 3.4.x, Maven.
- Build/test: `./mvnw clean test`
- Ejecutar local: `./mvnw spring-boot:run` (perfil `dev` por defecto).
- Si cambias infra/config, avisa para build de Docker.

## 🧩 Módulos verticales
- Fit: `MembershipController` (`@PreAuthorize("hasAuthority('FIT')")`).
- Style: `BoutiqueItemController` (`@PreAuthorize("hasAuthority('STYLE')")`).
- Features del JWT se derivan del `licenseKey` (deuda técnica, issue #46).

## 🔐 Notas críticas de seguridad
- IDOR en notificaciones corregido (issue #47, tests en `NotificationServiceTest`).
- Pendiente: normalizar prefijo `/api/v1/` en controladores (fit, style, notification, etc.).
- No hardcodear secretos; usar variables de entorno.

## 🐛 Issues abiertos relevantes
- #46: deuda técnica JWT features (agregar columna `features` a `license`).
- Normalización de rutas a `/api/v1/` (pendiente).
