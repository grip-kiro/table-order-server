-- 테스트용 매장
INSERT INTO stores (id, name, master_pin) VALUES (1, '맛있는 식당', '1234');

-- 테스트용 관리자
INSERT INTO admin_accounts (store_id, username, password_hash) VALUES (1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- 테스트용 테이블
INSERT INTO restaurant_tables (store_id, table_number, pin) VALUES (1, 1, '0000');
INSERT INTO restaurant_tables (store_id, table_number, pin) VALUES (1, 2, '0000');
INSERT INTO restaurant_tables (store_id, table_number, pin) VALUES (1, 3, '0000');

-- 카테고리
INSERT INTO categories (store_id, name, display_order) VALUES (1, '메인', 1);
INSERT INTO categories (store_id, name, display_order) VALUES (1, '사이드', 2);
INSERT INTO categories (store_id, name, display_order) VALUES (1, '음료', 3);
INSERT INTO categories (store_id, name, display_order) VALUES (1, '디저트', 4);

-- 메뉴
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '김치찌개', '깊고 얼큰한 국물의 돼지고기 김치찌개', 9000, NULL, 1);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '된장찌개', '구수하고 담백한 손두부 된장찌개', 8500, NULL, 2);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '삼겹살 (1인분)', '국내산 신선 삼겹살, 쌈채소 포함', 14000, NULL, 3);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '제육볶음', '매콤달콤 양념 돼지불고기', 10000, NULL, 4);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '계란말이', '부드러운 일본식 계란말이', 6000, NULL, 1);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '감자튀김', '바삭하게 튀긴 황금 감자튀김', 5000, NULL, 2);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '군만두', '겉은 바삭, 속은 촉촉한 고기만두', 6500, NULL, 3);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '콜라', '시원한 코카콜라 355ml', 2000, NULL, 1);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '사이다', '청량한 칠성사이다 355ml', 2000, NULL, 2);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '아이스 아메리카노', '진한 에스프레소 아이스 아메리카노', 3000, NULL, 3);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '식혜', '달콤하고 시원한 전통 식혜', 3500, NULL, 4);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '아이스크림', '부드러운 바닐라 소프트아이스크림', 3000, NULL, 1);
INSERT INTO menus (store_id, name, description, price, image_url, display_order) VALUES (1, '팥빙수', '달콤한 팥과 쫄깃한 떡이 가득', 7000, NULL, 2);

-- 메뉴-카테고리 매핑
INSERT INTO menu_categories (menu_id, category_id) VALUES (1, 1);
INSERT INTO menu_categories (menu_id, category_id) VALUES (2, 1);
INSERT INTO menu_categories (menu_id, category_id) VALUES (3, 1);
INSERT INTO menu_categories (menu_id, category_id) VALUES (4, 1);
INSERT INTO menu_categories (menu_id, category_id) VALUES (5, 2);
INSERT INTO menu_categories (menu_id, category_id) VALUES (6, 2);
INSERT INTO menu_categories (menu_id, category_id) VALUES (7, 2);
INSERT INTO menu_categories (menu_id, category_id) VALUES (8, 3);
INSERT INTO menu_categories (menu_id, category_id) VALUES (9, 3);
INSERT INTO menu_categories (menu_id, category_id) VALUES (10, 3);
INSERT INTO menu_categories (menu_id, category_id) VALUES (11, 3);
INSERT INTO menu_categories (menu_id, category_id) VALUES (12, 4);
INSERT INTO menu_categories (menu_id, category_id) VALUES (13, 4);
