# Migración: columna `features` en `license`

Hibernate (`ddl-auto=update`) crea/actualiza la columna automáticamente en dev.

Para entornos con migraciones manuales, ejecutar:

```sql
ALTER TABLE license ADD COLUMN features VARCHAR(255);
```

La columna almacena una lista de features separada por comas (ej. `FIT,STYLE`).  
`FeatureListConverter` se encarga de convertir entre `List<String>` (entidad) y `VARCHAR` (BD).