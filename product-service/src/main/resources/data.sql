-- Categorías
INSERT INTO categories (id, name, description, created_at) VALUES
('a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d', 'Electrónica', 'Dispositivos electrónicos y accesorios', NOW()),
('b2c3d4e5-f6a7-4b5c-9d0e-1f2a3b4c5d6e', 'Ropa', 'Prendas de vestir y accesorios de moda', NOW()),
('c3d4e5f6-a7b8-4c5d-0e1f-2a3b4c5d6e7f', 'Hogar', 'Artículos para el hogar y decoración', NOW()),
('d4e5f6a7-b8c9-4d5e-1f2a-3b4c5d6e7f8a', 'Deportes', 'Equipamiento deportivo y fitness', NOW()),
('e5f6a7b8-c9d0-4e5f-2a3b-4c5d6e7f8a9b', 'Libros', 'Libros físicos y digitales', NOW()),
('f6a7b8c9-d0e1-4f5a-3b4c-5d6e7f8a9b0c', 'Juguetes', 'Juguetes y entretenimiento infantil', NOW());

-- Productos de Electrónica
INSERT INTO products (id, name, description, price, stock, category_id, status, image_url, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Laptop HP Pavilion 15', 'Laptop con procesador Intel Core i5, 8GB RAM, 256GB SSD, pantalla 15.6"', 899.99, 15, 'a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d', 'ACTIVE', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500', NOW()),
('22222222-2222-2222-2222-222222222222', 'Mouse Logitech MX Master 3', 'Mouse inalámbrico ergonómico con precisión avanzada', 99.99, 50, 'a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d', 'ACTIVE', 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500', NOW()),
('33333333-3333-3333-3333-333333333333', 'Teclado Mecánico RGB', 'Teclado mecánico gaming con iluminación RGB personalizable', 129.99, 30, 'a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d', 'ACTIVE', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500', NOW()),
('44444444-4444-4444-4444-444444444444', 'Auriculares Sony WH-1000XM4', 'Auriculares con cancelación de ruido activa y audio de alta resolución', 349.99, 20, 'a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d', 'ACTIVE', 'https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=500', NOW()),
('55555555-5555-5555-5555-555555555555', 'Monitor LG 27" 4K', 'Monitor 4K UHD de 27 pulgadas con HDR10', 449.99, 12, 'a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d', 'ACTIVE', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500', NOW()),

-- Productos de Ropa
('66666666-6666-6666-6666-666666666666', 'Camiseta Nike Dri-FIT', 'Camiseta deportiva con tecnología de absorción de humedad', 29.99, 100, 'b2c3d4e5-f6a7-4b5c-9d0e-1f2a3b4c5d6e', 'ACTIVE', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500', NOW()),
('77777777-7777-7777-7777-777777777777', 'Jeans Levi''s 501', 'Jeans clásicos de corte recto, 100% algodón', 79.99, 60, 'b2c3d4e5-f6a7-4b5c-9d0e-1f2a3b4c5d6e', 'ACTIVE', 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=500', NOW()),
('88888888-8888-8888-8888-888888888888', 'Chaqueta North Face', 'Chaqueta impermeable para exteriores con aislamiento térmico', 189.99, 25, 'b2c3d4e5-f6a7-4b5c-9d0e-1f2a3b4c5d6e', 'ACTIVE', 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500', NOW()),
('99999999-9999-9999-9999-999999999999', 'Zapatillas Adidas Ultraboost', 'Zapatillas running con tecnología Boost para máximo retorno de energía', 159.99, 40, 'b2c3d4e5-f6a7-4b5c-9d0e-1f2a3b4c5d6e', 'ACTIVE', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500', NOW()),

-- Productos de Hogar
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Cafetera Nespresso', 'Cafetera de cápsulas con sistema de presión de 19 bares', 199.99, 18, 'c3d4e5f6-a7b8-4c5d-0e1f-2a3b4c5d6e7f', 'ACTIVE', 'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=500', NOW()),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Aspiradora Dyson V11', 'Aspiradora inalámbrica con tecnología ciclónica', 599.99, 10, 'c3d4e5f6-a7b8-4c5d-0e1f-2a3b4c5d6e7f', 'ACTIVE', 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=500', NOW()),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Lámpara LED Inteligente', 'Lámpara con control por app y 16 millones de colores', 49.99, 75, 'c3d4e5f6-a7b8-4c5d-0e1f-2a3b4c5d6e7f', 'ACTIVE', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=500', NOW()),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Set de Sartenes Antiadherentes', 'Juego de 3 sartenes con recubrimiento cerámico', 89.99, 35, 'c3d4e5f6-a7b8-4c5d-0e1f-2a3b4c5d6e7f', 'ACTIVE', 'https://images.unsplash.com/photo-1556909212-d5b604d0c90d?w=500', NOW()),

-- Productos de Deportes
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Bicicleta de Montaña Trek', 'Bicicleta MTB con suspensión completa y frenos de disco', 1299.99, 8, 'd4e5f6a7-b8c9-4d5e-1f2a-3b4c5d6e7f8a', 'ACTIVE', 'https://images.unsplash.com/photo-1576435728678-68d0fbf94e91?w=500', NOW()),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Mancuernas Ajustables 20kg', 'Par de mancuernas con peso ajustable de 5 a 20kg', 149.99, 22, 'd4e5f6a7-b8c9-4d5e-1f2a-3b4c5d6e7f8a', 'ACTIVE', 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=500', NOW()),
('10101010-1010-1010-1010-101010101010', 'Yoga Mat Premium', 'Esterilla de yoga antideslizante de 6mm de grosor', 39.99, 80, 'd4e5f6a7-b8c9-4d5e-1f2a-3b4c5d6e7f8a', 'ACTIVE', 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=500', NOW()),
('20202020-2020-2020-2020-202020202020', 'Reloj Deportivo Garmin', 'Smartwatch con GPS y monitor de frecuencia cardíaca', 299.99, 15, 'd4e5f6a7-b8c9-4d5e-1f2a-3b4c5d6e7f8a', 'ACTIVE', 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=500', NOW()),

-- Productos de Libros
('30303030-3030-3030-3030-303030303030', 'El Quijote - Edición Ilustrada', 'Edición especial ilustrada del clásico de Cervantes', 34.99, 45, 'e5f6a7b8-c9d0-4e5f-2a3b-4c5d6e7f8a9b', 'ACTIVE', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500', NOW()),
('40404040-4040-4040-4040-404040404040', 'Cien Años de Soledad', 'Obra maestra de Gabriel García Márquez', 24.99, 60, 'e5f6a7b8-c9d0-4e5f-2a3b-4c5d6e7f8a9b', 'ACTIVE', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500', NOW()),
('50505050-5050-5050-5050-505050505050', 'Sapiens - Yuval Noah Harari', 'De animales a dioses: Breve historia de la humanidad', 29.99, 70, 'e5f6a7b8-c9d0-4e5f-2a3b-4c5d6e7f8a9b', 'ACTIVE', 'https://images.unsplash.com/photo-1589998059171-988d887df646?w=500', NOW()),

-- Productos de Juguetes
('60606060-6060-6060-6060-606060606060', 'LEGO Star Wars Millennium Falcon', 'Set de construcción con 1351 piezas', 159.99, 20, 'f6a7b8c9-d0e1-4f5a-3b4c-5d6e7f8a9b0c', 'ACTIVE', 'https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=500', NOW()),
('70707070-7070-7070-7070-707070707070', 'Nintendo Switch OLED', 'Consola híbrida con pantalla OLED de 7 pulgadas', 349.99, 25, 'f6a7b8c9-d0e1-4f5a-3b4c-5d6e7f8a9b0c', 'ACTIVE', 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=500', NOW()),
('80808080-8080-8080-8080-808080808080', 'Cubo Rubik Original', 'Cubo mágico 3x3 oficial de velocidad', 14.99, 100, 'f6a7b8c9-d0e1-4f5a-3b4c-5d6e7f8a9b0c', 'ACTIVE', 'https://images.unsplash.com/photo-1591991731833-b8b1b2b5c2f4?w=500', NOW()),
('90909090-9090-9090-9090-909090909090', 'Peluche Gigante Oso', 'Oso de peluche suave de 1 metro de altura', 79.99, 15, 'f6a7b8c9-d0e1-4f5a-3b4c-5d6e7f8a9b0c', 'ACTIVE', 'https://images.unsplash.com/photo-1530325553241-4f6e7690cf36?w=500', NOW());
