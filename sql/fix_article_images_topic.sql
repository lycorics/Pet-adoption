-- ????????????????????????
SET NAMES utf8mb4;

UPDATE pro_pet.article SET content = REPLACE(content, 'http://127.0.0.1:9090/web/download/075072fd9de04d799704a82f95eb43da.jpg', 'http://127.0.0.1:9090/web/download/b206f81104ac41bf95ca0b78bf22fcf1.jpeg') WHERE id = 1;
UPDATE pro_pet.article SET content = REPLACE(content, 'http://127.0.0.1:9090/web/download/18f8f18ae3aa48ebaad1c71fc46e1927.jpg', 'http://127.0.0.1:9090/web/download/f7c30656d29b455ea6657a5e189c9305.webp') WHERE id = 2;
UPDATE pro_pet.article SET content = REPLACE(content, 'http://127.0.0.1:9090/web/download/20077de313f74bf9ae0249e8e5977ab5.png', 'http://127.0.0.1:9090/web/download/167c1fa2ec434c5692fcc472c6a9cfdb.jpeg') WHERE id = 4;
UPDATE pro_pet.article SET content = REPLACE(content, 'http://127.0.0.1:9090/web/download/7011196e691c40fb984e4fac9b4cc5b7.jpg', 'http://127.0.0.1:9090/web/download/ad28632073704b518b49ebb2f30d534a.jpg') WHERE id = 5;
UPDATE pro_pet.article SET content = REPLACE(content, 'http://127.0.0.1:9090/web/download/a549d6a9489a4af6ac8a48a6ab5b34cf.jpg', 'http://127.0.0.1:9090/web/download/f261c10b7ecb4afd9c9a4d59002aafa9.jpg') WHERE id = 5;
