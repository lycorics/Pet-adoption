-- fix article content external images (bobocw.com -> local files)
SET NAMES utf8mb4;

UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20220715/1657894542191216.jpeg', 'http://127.0.0.1:9090/web/download/075072fd9de04d799704a82f95eb43da.jpg') WHERE id = 1;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20220702/1656693309192088.jpeg', 'http://127.0.0.1:9090/web/download/18f8f18ae3aa48ebaad1c71fc46e1927.jpg') WHERE id = 2;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20220702/1656694682625092.jpeg', 'http://127.0.0.1:9090/web/download/1a3fe16dacd5438286c2d269cc9f4af2.jpg') WHERE id = 3;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20220701/1656670798127388.jpeg', 'http://127.0.0.1:9090/web/download/20077de313f74bf9ae0249e8e5977ab5.png') WHERE id = 4;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20230104/1672824021186441.jpeg', 'http://127.0.0.1:9090/web/download/4aeda9e30ee14d2bb5277b0af815a5ba.png') WHERE id = 5;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20230104/1672824078122062.jpeg', 'http://127.0.0.1:9090/web/download/51ca50efeebd422f90668202e70760b4.png') WHERE id = 5;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20230104/1672824343133196.jpeg', 'http://127.0.0.1:9090/web/download/67f73b5837a14f6fae5460f462ec48a5.png') WHERE id = 5;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20230104/1672824584130412.jpeg', 'http://127.0.0.1:9090/web/download/7011196e691c40fb984e4fac9b4cc5b7.jpg') WHERE id = 5;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20230104/1672824001622805.jpeg', 'http://127.0.0.1:9090/web/download/a549d6a9489a4af6ac8a48a6ab5b34cf.jpg') WHERE id = 5;
UPDATE pro_pet.article SET content = REPLACE(content, 'https://www.bobocw.com/uploads/ueditor/image/20220715/1657895161393631.jpeg', 'http://127.0.0.1:9090/web/download/b1eb78b47cc84ed6a93d2298e3b82919.jpg') WHERE id = 6;
