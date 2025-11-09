-- ===== (Opcional) Asegurar índices únicos, por si el esquema aún no los crea =====
-- Si ya existen, estas líneas no fallan.
CREATE UNIQUE INDEX IF NOT EXISTS ux_category_name ON category (name);
CREATE UNIQUE INDEX IF NOT EXISTS ux_product_barcode ON product (barcode);

-- ===== CATEGORÍAS (idempotente) =====
INSERT INTO category (name) VALUES ('Bebidas')           ON CONFLICT (name) DO NOTHING;
INSERT INTO category (name) VALUES ('Botanas')           ON CONFLICT (name) DO NOTHING;
INSERT INTO category (name) VALUES ('Lácteos')           ON CONFLICT (name) DO NOTHING;
INSERT INTO category (name) VALUES ('Abarrotes')         ON CONFLICT (name) DO NOTHING;
INSERT INTO category (name) VALUES ('Limpieza')          ON CONFLICT (name) DO NOTHING;
INSERT INTO category (name) VALUES ('Cuidado personal')  ON CONFLICT (name) DO NOTHING;

-- ===== PRODUCTOS (idempotente) =====
-- Nota: resolvemos category_id por nombre con subselect; evitamos fallos si corremos el script varias veces.
-- BEBIDAS
INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Agua 600ml','7501031311309', c.id, 12.50, 50, 10 FROM category c WHERE c.name='Bebidas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Agua 1.5L','7501031311408', c.id, 18.90, 40, 10 FROM category c WHERE c.name='Bebidas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Refresco cola 355ml','7501055300094', c.id, 15.00, 60, 12 FROM category c WHERE c.name='Bebidas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Refresco limón 600ml','7501055300193', c.id, 19.00, 45, 10 FROM category c WHERE c.name='Bebidas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Jugo naranja 1L','7501000111117', c.id, 28.50, 30, 8 FROM category c WHERE c.name='Bebidas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Isotónica 500ml','7501200000126', c.id, 22.00, 25, 6 FROM category c WHERE c.name='Bebidas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Café frío lata 240ml','7501250000450', c.id, 24.90, 20, 5 FROM category c WHERE c.name='Bebidas'
ON CONFLICT (barcode) DO NOTHING;

-- BOTANAS
INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Papas fritas 45g','7501026000012', c.id, 18.00, 30, 6 FROM category c WHERE c.name='Botanas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Papas adobadas 45g','7501026000111', c.id, 18.00, 28, 6 FROM category c WHERE c.name='Botanas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Palomitas mantequilla 85g','7502001000014', c.id, 21.00, 25, 6 FROM category c WHERE c.name='Botanas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Cacahuates 100g','7502100000107', c.id, 23.50, 22, 6 FROM category c WHERE c.name='Botanas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Botana de maíz 52g','7502200000213', c.id, 13.50, 35, 8 FROM category c WHERE c.name='Botanas'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Galletas saladas 90g','7502300000319', c.id, 14.90, 32, 8 FROM category c WHERE c.name='Botanas'
ON CONFLICT (barcode) DO NOTHING;

-- LÁCTEOS
INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Leche entera 1L','7501055900018', c.id, 28.90, 35, 8 FROM category c WHERE c.name='Lácteos'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Leche deslactosada 1L','7501055900025', c.id, 30.50, 30, 8 FROM category c WHERE c.name='Lácteos'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Yogur natural 1kg','7502400000411', c.id, 59.00, 15, 4 FROM category c WHERE c.name='Lácteos'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Queso panela 400g','7502500000517', c.id, 89.00, 12, 3 FROM category c WHERE c.name='Lácteos'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Mantequilla 90g','7502600000613', c.id, 24.00, 18, 4 FROM category c WHERE c.name='Lácteos'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Crema 450ml','7502700000719', c.id, 36.00, 16, 4 FROM category c WHERE c.name='Lácteos'
ON CONFLICT (barcode) DO NOTHING;

-- ABARROTES
INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Arroz 1kg','7503000001011', c.id, 29.90, 40, 10 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Frijol negro 900g','7503100001110', c.id, 34.90, 35, 10 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Azúcar 1kg','7503200001216', c.id, 31.50, 38, 10 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Harina de trigo 1kg','7503300001312', c.id, 28.00, 30, 8 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Aceite vegetal 1L','7503400001418', c.id, 49.00, 24, 6 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Atún en agua 140g','7503500001514', c.id, 19.90, 36, 8 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Puré de tomate 210g','7503600001610', c.id, 12.50, 28, 6 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Sal 1kg','7503700001716', c.id, 16.00, 26, 6 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Pasta espagueti 200g','7503800001812', c.id, 11.90, 34, 8 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Galletas maría 170g','7503900001918', c.id, 18.90, 22, 6 FROM category c WHERE c.name='Abarrotes'
ON CONFLICT (barcode) DO NOTHING;

-- LIMPIEZA
INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Detergente polvo 800g','7504000002014', c.id, 49.90, 20, 5 FROM category c WHERE c.name='Limpieza'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Detergente líquido 1L','7504100002110', c.id, 58.00, 18, 5 FROM category c WHERE c.name='Limpieza'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Suavizante 850ml','7504200002216', c.id, 44.00, 16, 4 FROM category c WHERE c.name='Limpieza'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Jabón barra 200g','7504300002312', c.id, 12.00, 40, 10 FROM category c WHERE c.name='Limpieza'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Cloro 1L','7504400002418', c.id, 22.00, 22, 6 FROM category c WHERE c.name='Limpieza'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Limpiador multiusos 1L','7504500002514', c.id, 32.50, 18, 5 FROM category c WHERE c.name='Limpieza'
ON CONFLICT (barcode) DO NOTHING;

-- CUIDADO PERSONAL
INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Shampoo 750ml','7504600002610', c.id, 69.00, 14, 4 FROM category c WHERE c.name='Cuidado personal'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Acondicionador 750ml','7504700002716', c.id, 72.00, 12, 3 FROM category c WHERE c.name='Cuidado personal'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Crema dental 100ml','7504800002812', c.id, 29.00, 20, 5 FROM category c WHERE c.name='Cuidado personal'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Cepillo dental medio','7504900002918', c.id, 25.00, 25, 6 FROM category c WHERE c.name='Cuidado personal'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Desodorante 100g','7505000003013', c.id, 48.00, 15, 4 FROM category c WHERE c.name='Cuidado personal'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Rastrillos 2 pzas','7505100003119', c.id, 27.00, 18, 4 FROM category c WHERE c.name='Cuidado personal'
ON CONFLICT (barcode) DO NOTHING;

INSERT INTO product (name, barcode, category_id, price, stock, min_stock)
SELECT 'Gel para cabello 250g','7505200003215', c.id, 35.00, 16, 4 FROM category c WHERE c.name='Cuidado personal'
ON CONFLICT (barcode) DO NOTHING;
