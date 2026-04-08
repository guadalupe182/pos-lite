<h1 align="center">POS-lite 🧾📦</h1>
<p align="center">
  <strong>Spring Boot</strong> • <strong>Next.js</strong> • <strong>PostgreSQL</strong> • <strong>JWT</strong> • <strong>Docker</strong> • <strong>Render</strong> • <strong>Vercel</strong>
</p>

<p align="center">
  <a href="#-características">Características</a> •
  <a href="#-demo-en-vivo">Demo</a> •
  <a href="#-tecnologías">Tecnologías</a> •
  <a href="#-despliegue">Despliegue</a> •
  <a href="#-api-documentation">API</a> •
  <a href="#-changelog">Changelog</a>
</p>

---

> **TL;DR**: Sistema de punto de venta completo con escáner de códigos de barras (cámara web/móvil), gestión de inventario, ventas, reportes exportables a Excel/PDF, autenticación JWT y despliegue en la nube.

## ✨ Características

- 🔐 **Autenticación JWT** con cookie HttpOnly (segura y stateless).
- 🛒 **CRUD completo** de productos, categorías y packs.
- 📷 **Escáner de códigos de barras** (cámara web o móvil) con alta rápida.
- 📊 **Dashboard** con estadísticas clave (productos, ventas, stock bajo).
- 📈 **Reportes** de inventario (con alerta de stock bajo) y ventas (con gráficas por día/mes).
- 📎 **Exportación** a Excel y PDF.
- 🧾 **Registro de ventas** con carrito interactivo.
- 🐳 **Docker** para entorno de desarrollo.
- 🌐 **Desplegado en la nube**: backend en Render, frontend en Vercel.

## 🚀 Demo en vivo

👉 **Frontend**: [https://pos-lite-front.vercel.app](https://pos-lite-front.vercel.app)

**Credenciales de prueba:**
- Usuario: `user@demo.com`
- Contraseña: `123`

> ⏱️ *Nota: El backend está en plan gratuito (Render). La primera carga puede tardar hasta 50 segundos mientras el servicio "despierta".*

## 🛠️ Tecnologías

| Área          | Tecnologías |
|---------------|-------------|
| Backend       | Spring Boot, Spring Security, JWT, JPA/Hibernate, PostgreSQL |
| Frontend      | Next.js (App Router), React, Tailwind CSS, Chart.js |
| Escáner       | html5-qrcode |
| Reportes      | xlsx, jspdf, jspdf-autotable |
| Despliegue    | Docker, Render (backend), Vercel (frontend) |
| Herramientas  | Maven, Git, GitHub |

## 📦 Despliegue

- **Backend**: Render (Docker + PostgreSQL en Neon).
- **Frontend**: Vercel (Next.js).
- **Base de datos**: Neon (PostgreSQL serverless).

## 📖 Documentación de la API

La API está documentada automáticamente con **OpenAPI (Swagger)**. Cuando ejecutas el backend localmente, puedes explorar y probar todos los endpoints en:
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Los principales endpoints son:
- `POST /api/auth/register` – Registrar usuario.
- `POST /api/auth/login` – Iniciar sesión (devuelve cookie HttpOnly).
- `POST /api/auth/logout` – Cerrar sesión.
- `GET /api/products` – Listar productos.
- `POST /api/products` – Crear producto.
- `PUT /api/products/{id}` – Actualizar producto.
- `DELETE /api/products/{id}` – Eliminar producto.
- `GET /api/products/barcode/{code}` – Buscar producto por código.
- `POST /api/products/adjust-by-barcode` – Ajustar stock (entrada/salida) por código.
- `POST /api/sales` – Registrar venta.
- `GET /api/sales/report` – Reporte de ventas por rango de fechas.
- `GET /api/sales/inventory-report` – Reporte de inventario con stock bajo.
- `GET /api/categories` – Listar categorías.

*(Para más detalles, explora los controladores en el código fuente.)*

## 🐳 Ejecutar localmente con Docker

```bash
# Clonar el repositorio
git clone https://github.com/guadalupe182/pos-lite.git
cd pos-lite

# Configurar base de datos (opcional, usa tu propia instancia o la de Neon)
# Crear archivo application-dev.properties con tus credenciales

# Ejecutar con Maven
./mvnw spring-boot:run

# O con Docker (requiere Dockerfile)
docker build -t pos-lite .
docker run -p 8080:8080 pos-lite