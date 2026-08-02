-- Usuario Admin (password: admin123)
INSERT INTO app_user (id, email, password, enabled, created) VALUES
    (1, 'admin@example.com', '$2a$10$e882tH.p372I48gWvDGB4.I8oF00a75S.M4k2D4r.bC7P/cB8P2/m', true, NOW())
ON CONFLICT (id) DO NOTHING;

-- Roles de Usuario
INSERT INTO app_user_roles (user_id, role) VALUES
    (1, 'ADMIN')
ON CONFLICT DO NOTHING;

-- Categorías
INSERT INTO category (id, name) VALUES
                                    (1, 'Periféricos y Accesorios'),
                                    (2, 'Componentes PC'),
                                    (3, 'Almacenamiento'),
                                    (4, 'Redes y Conectividad')
ON CONFLICT (id) DO NOTHING;

-- Productos
INSERT INTO product (id, barcode, name, price, stock, min_stock, category_id) VALUES
                                                                                  (1, '7501001', 'Mouse Logitech G203 Lightsync', 550.00, 25, 5, 1),
                                                                                  (2, '7501002', 'Teclado Mecánico Keychron K2', 1850.00, 12, 3, 1),
                                                                                  (3, '7501003', 'Monitor Dell UltraSharp 24"', 4200.00, 8, 2, 1),
                                                                                  (4, '7501004', 'Audífonos HyperX Cloud II', 1600.00, 15, 3, 1),
                                                                                  (5, '7501005', 'Webcam Logitech C920 HD', 1200.00, 10, 2, 1),
                                                                                  (6, '7502001', 'Procesador AMD Ryzen 5 5600X', 3200.00, 14, 4, 2),
                                                                                  (7, '7502002', 'Memoria RAM Corsair Vengeance 16GB', 950.00, 30, 10, 2),
                                                                                  (8, '7503001', 'SSD NVMe Kingston 1TB', 1050.00, 40, 10, 3),
                                                                                  (9, '7504001', 'Router TP-Link Archer AX1500', 1250.00, 18, 3, 4)
ON CONFLICT (id) DO NOTHING;

-- Ventas
INSERT INTO sale (id, sale_date, total, user_id) VALUES
                                                     (1, NOW() - INTERVAL '6 days', 2400.00, 1),
                                                     (2, NOW() - INTERVAL '5 days', 1750.00, 1),
                                                     (3, NOW() - INTERVAL '4 days', 4200.00, 1),
                                                     (4, NOW() - INTERVAL '3 days', 2150.00, 1),
                                                     (5, NOW() - INTERVAL '2 days', 3100.00, 1),
                                                     (6, NOW() - INTERVAL '1 day', 1800.00, 1)
ON CONFLICT (id) DO NOTHING;

-- Detalles de Ventas
INSERT INTO sale_detail (sale_id, product_id, quantity, unit_price, subtotal) VALUES
                                                                                  (1, 1, 2, 550.00, 1100.00), (1, 9, 1, 1250.00, 1250.00),
                                                                                  (2, 1, 1, 550.00, 550.00), (2, 5, 1, 1200.00, 1200.00),
                                                                                  (3, 3, 1, 4200.00, 4200.00),
                                                                                  (4, 2, 1, 1850.00, 1850.00), (4, 1, 1, 550.00, 550.00),
                                                                                  (5, 6, 1, 3200.00, 3200.00),
                                                                                  (6, 4, 1, 1600.00, 1600.00);

-- Sincronizar Secuencias en PostgreSQL
SELECT setval('category_id_seq', COALESCE((SELECT MAX(id) FROM category), 1));
SELECT setval('product_id_seq', COALESCE((SELECT MAX(id) FROM product), 1));
SELECT setval('sale_id_seq', COALESCE((SELECT MAX(id) FROM sale), 1));
SELECT setval('sale_detail_id_seq', COALESCE((SELECT MAX(id) FROM sale_detail), 1));
SELECT setval('app_user_id_seq', COALESCE((SELECT MAX(id) FROM app_user), 1));