# POS-lite 🧾📦

<p align="center">
  <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="MIT License">
  <img src="https://img.shields.io/badge/Commercial-License_Required-blue.svg" alt="Commercial License Required">
</p>

<p align="center">
  <strong>Spring Boot</strong> • <strong>Next.js</strong> • <strong>PostgreSQL</strong> • <strong>JWT</strong> • <strong>Docker</strong> • <strong>AWS EC2</strong> • <strong>Vercel</strong> • <strong>Cloudflare</strong>
</p>

<p align="center">
  <a href="#-características">Características</a> •
  <a href="#-demo-en-vivo">Demo</a> •
  <a href="#-tecnologías">Tecnologías</a> •
  <a href="#-arquitectura">Arquitectura</a> •
  <a href="#-despliegue">Despliegue</a> •
  <a href="#-documentación-de-la-api">API</a> •
  <a href="#-changelog">Changelog</a>
</p>

---

> **TL;DR**: Sistema de punto de venta completo con escáner de códigos de barras (cámara web/móvil), gestión de inventario, ventas, reportes exportables a Excel/PDF, autenticación JWT y despliegue en la nube con AWS EC2, Vercel y Cloudflare.

## ✨ Características

- 🔐 **Autenticación JWT** con cookie HttpOnly (segura y stateless).
- 🛒 **CRUD completo** de productos, categorías y packs.
- 📷 **Escáner de códigos de barras** (cámara web o móvil) con alta rápida.
- 📊 **Dashboard** con estadísticas clave (productos, ventas, stock bajo).
- 📈 **Reportes** de inventario (con alerta de stock bajo) y ventas (con gráficas por día/mes).
- 📎 **Exportación** a Excel y PDF.
- 🧾 **Registro de ventas** con carrito interactivo.
- 🐳 **Docker** para entorno de desarrollo y despliegue alternativo.
- 🌐 **Desplegado en la nube**: backend en AWS EC2, frontend en Vercel, DNS/SSL con Cloudflare.

## 🚀 Demo en vivo

- **Frontend (POS-lite)**: [https://pos-lite-front.vercel.app](https://pos-lite-front.vercel.app)
- **Backend API**: [https://guadaluperosas.com](https://guadaluperosas.com) (responde JSON)
- **Portafolio personal**: [https://www.guadaluperosas.com](https://www.guadaluperosas.com)

**Credenciales de prueba (para la demo):**  
- Usuario: `prueba123@example.com`  
- Contraseña: `123456`

> ⏱️ *Nota: El backend está en AWS EC2 (plan gratuito t2.micro). La instancia puede tardar unos segundos en responder si estuvo inactiva.*

## 🛠️ Tecnologías

| Área          | Tecnologías |
|---------------|-------------|
| Backend       | Spring Boot, Spring Security, JWT, JPA/Hibernate, PostgreSQL |
| Frontend      | Next.js (App Router), React, Tailwind CSS, Chart.js |
| Escáner       | html5-qrcode |
| Reportes      | xlsx, jspdf, jspdf-autotable |
| Despliegue    | Docker, AWS EC2, Vercel, Cloudflare, Nginx |
| Herramientas  | Maven, Git, GitHub |

## 🏗️ Arquitectura

- **Frontend**: Next.js desplegado en Vercel (SSL automático).
- **Backend**: Spring Boot corriendo en una instancia AWS EC2 (t2.micro) con IP elástica.
- **Base de datos**: PostgreSQL instalado en la misma EC2 (conexión local).
- **Proxy inverso**: Nginx en la EC2 para redirigir tráfico del puerto 80 al 8080.
- **DNS y SSL**: Cloudflare (modo Flexible) proporciona HTTPS y protección DDoS.

## 📦 Despliegue

### 🔹 Opción 1: AWS EC2

1. Crear instancia (Amazon Linux 2023)
2. Instalar:
   - Java 17
   - PostgreSQL
   - Nginx
3. Configurar base de datos (`pg_hba.conf`)
4. Subir `.jar` con `scp`
5. Ejecutar con `screen`
6. Configurar Nginx (proxy inverso)
7. Conectar dominio con Cloudflare

### 🔹 Opción 2: Docker

```bash
docker build -t pos-lite .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod pos-lite
```


## 📖 Documentación de la API

La API está documentada automáticamente con **OpenAPI (Swagger)** cuando se ejecuta en perfil `dev`. En producción (perfil `prod`) está deshabilitada por seguridad.

Para explorar la API localmente (perfil `dev`), ejecuta el backend y visita:  
👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Endpoints principales

#### 🔐 Autenticación

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|----------------|
| `POST` | `/api/auth/register` | Registrar un nuevo usuario | Pública |
| `POST` | `/api/auth/login` | Iniciar sesión (devuelve cookie HttpOnly) | Pública |
| `POST` | `/api/auth/logout` | Cerrar sesión | Requerida |

#### 📦 Productos y Categorías

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|----------------|
| `GET` | `/api/products` | Listar todos los productos | Requerida |
| `POST` | `/api/products` | Crear un nuevo producto | Requerida (ADMIN) |
| `PUT` | `/api/products/{id}` | Actualizar producto | Requerida (ADMIN) |
| `DELETE` | `/api/products/{id}` | Eliminar producto | Requerida (ADMIN) |
| `GET` | `/api/products/barcode/{code}` | Buscar producto por código de barras | Requerida |
| `POST` | `/api/products/adjust-by-barcode` | Ajustar stock (entrada/salida) por código | Requerida |
| `GET` | `/api/categories` | Listar categorías | Requerida |

#### 💰 Ventas y Reportes

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|----------------|
| `POST` | `/api/sales` | Registrar una venta | Requerida |
| `GET` | `/api/sales/report` | Reporte de ventas por rango de fechas | Requerida (ADMIN) |
| `GET` | `/api/sales/inventory-report` | Reporte de inventario con stock bajo | Requerida (ADMIN) |

#### 🏠 Utilidades

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|----------------|
| `GET` | `/` | Mensaje de bienvenida (JSON) | Pública |

### 📡 Ejemplo de petición con `curl`

**Registro de usuario:**
```bash

```bash
curl -X POST https://guadaluperosas.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@example.com","password":"123456"}'
Inicio de sesión (guarda cookie):

bash
curl -X POST https://guadaluperosas.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@example.com","password":"123456"}' \
  -c cookies.txt
Obtener productos (usando cookie):

bash
curl -b cookies.txt https://guadaluperosas.com/api/products
```

----

## 📝 Changelog
Ver [CHANGELOG.md](./CHANGELOG.md) para detalles de versiones.

## 📜 Licencia [Licencia](./LICENSE)

Este proyecto tiene un modelo de **licencia dual**:

- **Para fines académicos, educativos y open source**: [MIT License](./LICENSE).
- **Para usos comerciales**: Se requiere una [licencia comercial](./COMMERCIAL-LICENSE.txt).  
  Contacto: contacto@guadaluperosas.com

## 👥 Contribuciones

Ver [CONTRIBUTING.md](./CONTRIBUTING.md) para guía de colaboración.

MIT © 2026 Guadalupe Rosas
