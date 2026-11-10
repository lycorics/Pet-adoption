SET NAMES utf8mb4;
UPDATE pro_pet.article SET content = REPLACE(content, 'http://127.0.0.1:9090/web/download/67f73b5837a14f6fae5460f462ec48a5.png', 'http://127.0.0.1:9090/web/download/eb818406b4ad413c86e37e972a0fd6fa.jpeg') WHERE id = 5;
