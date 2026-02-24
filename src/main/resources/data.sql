-- Insert Dummy User (Seller)
INSERT IGNORE INTO users (user_id, email, password_hash, full_name, role, status, email_verified, created_at) VALUES 
(1, 'shop@ngayle.com', '$2a$10$wT.f.q/..', 'NgayLe Shop', 'SELLER', 'ACTIVE', true, NOW());

-- Insert Dummy Profile (Optional but good for consistency)
INSERT IGNORE INTO user_profiles (profile_id, user_id, bio) VALUES (1, 1, 'Official Shop of NgayLe.com');

-- Insert Dummy Shop
INSERT IGNORE INTO shops (shop_id, owner_id, shop_name, shop_slug, description, status, rating, created_at) VALUES 
(1, 1, 'NgayLe Official', 'ngayle-official', 'Gian hàng chính hãng cung cấp quà tặng', 'ACTIVE', 5.0, NOW());

-- Insert Categories (ROOT)
INSERT IGNORE INTO categories (category_id, name, slug, level, icon_url, display_order) VALUES
(1, 'Lễ Tết', 'le-tet', 1, 'celebration', 1),
(2, 'Sinh Nhật', 'qua-sinh-nhat', 1, 'cake', 2),
(3, 'Quà Cưới', 'qua-cuoi', 1, 'diamond', 3),
(4, 'Tặng Mẹ', 'qua-tang-me', 1, 'volunteer_activism', 4),
(5, 'Hoa Tươi', 'hoa-tuoi', 1, 'local_florist', 5),
(6, 'Đồ Trang Trí', 'do-trang-tri', 1, 'home_work', 6),
(7, 'Đặc Sản', 'dac-san', 1, 'ramen_dining', 7),
(8, 'Trang Sức', 'trang-suc', 1, 'watch', 8);

-- Insert Sub-Categories (Level 2)
-- Children of Lễ Tết (1)
INSERT IGNORE INTO categories (category_id, parent_id, name, slug, level, icon_url, display_order) VALUES
(11, 1, 'Hộp Quà Tết', 'hop-qua-tet', 2, 'inventory_2', 1),
(12, 1, 'Lì Xì', 'li-xi', 2, 'mark_email_unread', 2),
(13, 1, 'Mứt & Hạt', 'mut-hat', 2, 'nutrition', 3),
(14, 1, 'Rượu Vang', 'ruou-vang', 2, 'wine_bar', 4);

-- Children of Sinh Nhật (2)
INSERT IGNORE INTO categories (category_id, parent_id, name, slug, level, icon_url, display_order) VALUES
(21, 2, 'Bánh Kem', 'banh-kem', 2, 'cake', 1),
(22, 2, 'Gấu Bông', 'gau-bong', 2, 'pets', 2),
(23, 2, 'Thiệp Mừng', 'thiep-mung', 2, 'card_giftcard', 3);

-- Children of Hoa Tươi (5)
INSERT IGNORE INTO categories (category_id, parent_id, name, slug, level, icon_url, display_order) VALUES
(51, 5, 'Hoa Hồng', 'hoa-hong', 2, 'local_florist', 1),
(52, 5, 'Hoa Hướng Dương', 'hoa-huong-duong', 2, 'wb_sunny', 2),
(53, 5, 'Lan Hồ Điệp', 'lan-ho-diep', 2, 'spa', 3);


-- Insert dummy products
INSERT IGNORE INTO products (product_id, name, description, base_price, promotional_price, is_holiday_suggestion, status, rating, review_count, category_id, shop_id, created_at, updated_at) VALUES 
(1, 'Hộp Quà Tết Sum Vầy', 'Hộp quà tết cao cấp...', 1200000, 990000, true, 'ACTIVE', 4.8, 120, 11, 1, NOW(), NOW()), -- Cat 11
(2, 'Bó Hoa Hồng Nhập Khẩu', 'Hoa hồng Ecuador...', 850000, NULL, true, 'ACTIVE', 4.9, 50, 51, 1, NOW(), NOW()), -- Cat 51
(3, 'Bánh Kem Matcha', 'Bánh kem vị trà xanh...', 350000, 300000, true, 'ACTIVE', 4.5, 20, 21, 1, NOW(), NOW()), -- Cat 21
(4, 'Dây Chuyền Bạc', 'Dây chuyền bạc Ý...', 550000, 450000, true, 'ACTIVE', 4.7, 85, 8, 1, NOW(), NOW()), -- Cat 8
(5, 'Set Quà Sức Khỏe Cho Mẹ', 'Yến sào, nhân sâm...', 2500000, 2100000, true, 'ACTIVE', 5.0, 200, 4, 1, NOW(), NOW()), -- Cat 4
(6, 'Đèn Lồng Hội An', 'Đèn lồng thủ công...', 150000, NULL, true, 'ACTIVE', 4.6, 15, 6, 1, NOW(), NOW()); -- Cat 6

-- Insert dummy product variants
INSERT IGNORE INTO product_variants (variant_id, product_id, sku, name, price, stock_quantity, image_url) VALUES 
(1, 1, 'SKU001', 'Hộp đỏ', 990000, 100, 'https://salt.tikicdn.com/cache/750x750/ts/product/0a/61/5a/8f8c4c77c8e8c89c882193b8f6c6d059.jpg'),
(2, 2, 'SKU002', 'Bó 20 bông', 850000, 50, 'https://salt.tikicdn.com/cache/750x750/ts/product/2e/29/7a/7f0858e392658824f6010534c000df82.jpg'),
(3, 3, 'SKU003', 'Size 20cm', 300000, 20, 'https://salt.tikicdn.com/cache/750x750/ts/product/3a/0c/3e/1649987f6517865228555e378c66e288.jpg'),
(4, 4, 'SKU004', 'Mặt tròn', 450000, 15, 'https://salt.tikicdn.com/cache/750x750/ts/product/5e/1e/85/3c33276634c0b49746777c2763371994.jpg'),
(5, 5, 'SKU005', 'Hộp quà tiêu chuẩn', 2100000, 30, 'https://salt.tikicdn.com/cache/750x750/ts/product/88/5f/1f/263301389808603681428588001d9f8c.jpg'),
(6, 6, 'SKU006', 'Màu đỏ', 150000, 100, 'https://salt.tikicdn.com/cache/750x750/ts/product/78/34/00/f6597148293739506663584898239088.jpg');

-- Insert product images (thumbnails)
INSERT IGNORE INTO product_images (image_id, product_id, image_url, is_thumbnail, display_order) VALUES
(1, 1, 'https://salt.tikicdn.com/cache/750x750/ts/product/0a/61/5a/8f8c4c77c8e8c89c882193b8f6c6d059.jpg', true, 1),
(2, 2, 'https://salt.tikicdn.com/cache/750x750/ts/product/2e/29/7a/7f0858e392658824f6010534c000df82.jpg', true, 1),
(3, 3, 'https://salt.tikicdn.com/cache/750x750/ts/product/3a/0c/3e/1649987f6517865228555e378c66e288.jpg', true, 1),
(4, 4, 'https://salt.tikicdn.com/cache/750x750/ts/product/5e/1e/85/3c33276634c0b49746777c2763371994.jpg', true, 1),
(5, 5, 'https://salt.tikicdn.com/cache/750x750/ts/product/88/5f/1f/263301389808603681428588001d9f8c.jpg', true, 1),
(6, 6, 'https://salt.tikicdn.com/cache/750x750/ts/product/78/34/00/f6597148293739506663584898239088.jpg', true, 1);
