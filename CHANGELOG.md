# Changelog
Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/) y versionado con [SemVer](https://semver.org/lang/es/).

## [Unreleased]

## [v0.5.0] - 2025-11-16
### Added
- Alta rápida con `categoryName` (único) + `minStock` por producto.
- Modos `delta` o `op/qty` con exclusión mutua y validaciones.

### Fixed
- Idempotencia (categoría/barcode) para evitar duplicados.

### Changed
- Respuestas 400/404/409 con mensajes claros.
- `scanner.html` muestra formulario de Alta Rápida.

[Unreleased]: https://github.com/guadalupe182/pos-lite/compare/v0.5.0...HEAD
[v0.5.0]: https://github.com/guadalupe182/pos-lite/releases/tag/v0.5.0
