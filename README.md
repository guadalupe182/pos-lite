<!--
  POS-lite README
  Author: Guadalupe Rosas (Adrián Rosas)
  Version: v0.5.0
-->

<h1 align="center">POS‑lite 🧾📦</h1>
<p align="center">
  <strong>Spring Boot</strong> • <strong>ZXing</strong> • <strong>MySQL/MariaDB</strong> • <strong>Ngrok</strong>
</p>

<p align="center">
  <a href="#-caracter%C3%ADsticas">Características</a> •
  <a href="#-demo-rápida">Demo</a> •
  <a href="#-api-ajuste-de-inventario-v050">API</a> •
  <a href="#-scannerhtml-mini-ui">Scanner</a> •
  <a href="#-errores-comunes">Errores</a> •
  <a href="#-changelog">Changelog</a>
</p>

---

> **TL;DR**: Escanea códigos con la cámara del celular, **descuenta stock** al vuelo y si el producto **no existe** lo puedes dar de alta **desde el escáner** (incluye crear **nueva categoría** por nombre y configurar **minStock**).

## ✨ Características
- ⚡ **Alta Rápida** por escaneo: crea producto si el `barcode` no existe.
- 🏷️ Crear **nueva categoría** por `categoryName` (único por nombre).
- 📉 **Auto‑decremento** de stock al escanear (modo venta).
- 🛎️ **minStock** configurable por producto (alerta de bajo inventario).
- 🔒 **Idempotencia**: evita duplicados por `barcode`/categoría.
- 🧰 Backend **Spring Boot** + endpoints JSON.
- 🎥 Frontend ligero `scanner.html` con **ZXing** (sin build tools).
- 🌐 Soporte **ngrok** para usar el móvil como lector.

## 🚀 Demo rápida
Requisitos: **Java 17+, Maven 3.9+, MySQL/MariaDB** configurado en `application.properties`.

```bash
# 1) Ejecutar
mvn spring-boot:run

# 2) Abrir el scanner
#   Local:     http://localhost:8080/scanner.html
#   Con ngrok: (opcional)
#   ngrok http 8080
#   → https://TU-SUBDOMINIO.ngrok-free.app/scanner.html
```

En el scanner:
1) Guarda credenciales **Basic Auth** (ej. `admin:admin`).  
2) “Iniciar cámara” → apunta al código.  
3) Si el producto **no existe**, verás el formulario de **Alta Rápida**.

---

## 🧠 API: Ajuste de inventario (v0.5.0)
**Endpoint**: `POST /api/products/adjust-by-barcode`

### Modos (exclusión mutua)
- **A)** `op` = `IN | OUT` **+** `qty` (> 0)  
- **B)** `delta` (positivo = IN, negativo = OUT)

### Alta Rápida (si el barcode no existe)
Debes enviar `name`, `price` y **una categoría** vía `categoryId` **o** `categoryName`. Opcional: `minStock` (umbral de bajo inventario por producto).

#### Ejemplo: nueva categoría + minStock
```json
{
  "barcode": "7501234567890",
  "delta": 5,
  "reason": "INBOUND",
  "name": "Producto Alta Rápida",
  "categoryName": "Bebidas energéticas",
  "price": 19.90,
  "minStock": 12
}
```

### Ejemplos `curl`

**Alta Rápida (crea categoryName si no existe)**
```bash
curl -u admin:admin -H "Content-Type: application/json" \
  -d '{
        "barcode":"7501234567890",
        "delta": 5,
        "reason":"INBOUND",
        "name":"Producto Alta Rápida",
        "categoryName":"Bebidas energéticas",
        "price": 19.90,
        "minStock": 12
      }' \
  http://localhost:8080/api/products/adjust-by-barcode
```

**Ajuste por `delta` (venta: -2)**
```bash
curl -u admin:admin -H "Content-Type: application/json" \
  -d '{"barcode":"7501234567890","delta":-2,"reason":"SALE"}' \
  http://localhost:8080/api/products/adjust-by-barcode
```

**Ajuste por `op`/`qty` (salida de 2)**
```bash
curl -u admin:admin -H "Content-Type: application/json" \
  -d '{"barcode":"7501234567890","op":"OUT","qty":2}' \
  http://localhost:8080/api/products/adjust-by-barcode
```

---

## 🖥️ `scanner.html` (mini UI)
- **Auto‑decremento**: “Restar 1 al escanear” (usa `PATCH /api/products/{id}/decrement?qty=N`).
- **Alta Rápida** al no encontrar el producto:
  - `name`, `categoryId` **o** `categoryName` (crea si no existe),
  - `price`, `minStock`, y **cantidad a entrar** (`delta`>0).
- Linterna (si el dispositivo la soporta) y **beep** al éxito.
- Muestra en vivo `status` y el JSON de respuesta.

> **Tip**: El umbral `minStock` sirve para reportes o alertas de “por agotarse”. El valor por defecto es **10** si no se envía al crear.

---

## 🧯 Errores comunes
- **400 Bad Request**
  - `price >= 0`
  - `qty/delta > 0`
  - “Proveer (op y qty) o delta, pero no ambos”
  - “Falta categoryId o categoryName”
- **404 Not Found**
  - Barcode no existe **y** no enviaste datos mínimos para **Alta Rápida**.
- **409 Conflict**
  - “Sin stock suficiente”
  - Choques por `barcode` único (idempotente: reintenta/consulta y evita duplicados).

---

## 🛠️ Desarrollo
Flujo sugerido (git‑flow light):
```bash
# Crear feature
git checkout -b feature/nombre-feature

# Commit (convencional)
git commit -m "feat(scope): mensaje corto"

# Merge → main
git checkout main
git merge --no-ff feature/nombre-feature -m "Merge feature/nombre-feature"
git tag -a vX.Y.Z -m "Notas de release"
git push origin main --tags
```

---

## 📝 Changelog

### v0.5.0
- **feat**: Alta rápida con `categoryName` (único) + `minStock` por producto.
- **feat**: Modos `delta` o `op/qty` con exclusión mutua y validaciones.
- **fix**: Idempotencia (categoría/barcode) para evitar duplicados.
- **err**: Respuestas `400/404/409` con mensajes claros.
- **ui**: `scanner.html` muestra formulario de Alta Rápida.

---

## 📄 Licencia
MIT © 2025 Guadalupe Rosas
