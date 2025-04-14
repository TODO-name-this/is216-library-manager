SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `nextjslibrarydatabase` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `nextjslibrarydatabase`;

-- Clear existing tables if they exist
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `RESERVATION`;
DROP TABLE IF EXISTS `TRANSACTION`;
DROP TABLE IF EXISTS `RETURN_DETAIL`;
DROP TABLE IF EXISTS `REVIEW`;
DROP TABLE IF EXISTS `INVENTORY`;
DROP TABLE IF EXISTS `BOOK_CATEGORY`;
DROP TABLE IF EXISTS `BOOK_AUTHOR`;
DROP TABLE IF EXISTS `BOOK`;
DROP TABLE IF EXISTS `AUTHOR`;
DROP TABLE IF EXISTS `CATEGORY`;
DROP TABLE IF EXISTS `PUBLISHER`;
DROP TABLE IF EXISTS `USER`;
SET FOREIGN_KEY_CHECKS = 1;

-- Create tables
CREATE TABLE `AUTHOR` (
  `ID` varchar(36) NOT NULL,
  `AVATAR_URL` text DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  `BIRTHDAY` varchar(50) DEFAULT NULL,
  `BIOGRAPHY` text DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `CATEGORY` (
  `ID` varchar(36) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `DESCRIPTION` text DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `PUBLISHER` (
  `ID` varchar(36) NOT NULL,
  `LOGO_URL` text DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  `ADDRESS` text DEFAULT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `PHONE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `USER` (
  `ID` varchar(36) NOT NULL,
  `CCCD` char(15) NOT NULL,
  `DOB` varchar(50) NOT NULL,
  `AVATAR_URL` text DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `ROLE` varchar(50) DEFAULT 'USER',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `EMAIL_UNIQUE` (`EMAIL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `BOOK` (
  `ID` varchar(36) NOT NULL,
  `IMAGE_URL` text DEFAULT NULL,
  `TITLE` varchar(255) NOT NULL,
  `ISBN` varchar(50) DEFAULT NULL,
  `RATING_COUNT` int(11) NOT NULL DEFAULT 0,
  `RATING` float NOT NULL DEFAULT 0.0,
  `PUBLISHED_DATE` varchar(50) DEFAULT NULL,
  `PUBLISHER_ID` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BOOK_PUBLISHER` (`PUBLISHER_ID`),
  CONSTRAINT `FK_BOOK_PUBLISHER` FOREIGN KEY (`PUBLISHER_ID`) REFERENCES `PUBLISHER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `BOOK_AUTHOR` (
  `BOOK_ID` varchar(36) NOT NULL,
  `AUTHOR_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`BOOK_ID`, `AUTHOR_ID`),
  KEY `FK_BA_AUTHOR` (`AUTHOR_ID`),
  CONSTRAINT `FK_BA_BOOK` FOREIGN KEY (`BOOK_ID`) REFERENCES `BOOK` (`ID`),
  CONSTRAINT `FK_BA_AUTHOR` FOREIGN KEY (`AUTHOR_ID`) REFERENCES `AUTHOR` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `BOOK_CATEGORY` (
  `BOOK_ID` varchar(36) NOT NULL,
  `CATEGORY_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`BOOK_ID`, `CATEGORY_ID`),
  KEY `FK_BC_CATEGORY` (`CATEGORY_ID`),
  CONSTRAINT `FK_BC_BOOK` FOREIGN KEY (`BOOK_ID`) REFERENCES `BOOK` (`ID`),
  CONSTRAINT `FK_BC_CATEGORY` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `CATEGORY` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `INVENTORY` (
  `BOOK_ID` varchar(36) NOT NULL,
  `QUANTITY` int(11) NOT NULL DEFAULT 0,
  `BORROWED` bigint NOT NULL DEFAULT 0,
  `DAMAGED` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`BOOK_ID`),
  CONSTRAINT `FK_INVENTORY_BOOK` FOREIGN KEY (`BOOK_ID`) REFERENCES `BOOK` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `REVIEW` (
  `ID` varchar(36) NOT NULL,
  `USER_ID` varchar(36) NOT NULL,
  `BOOK_ID` varchar(36) NOT NULL,
  `STAR` int(11) NOT NULL,
  `COMMENT` text DEFAULT NULL,
  `DATE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_USER_BOOK` (`USER_ID`, `BOOK_ID`),
  KEY `FK_REVIEW_BOOK` (`BOOK_ID`),
  CONSTRAINT `FK_REVIEW_USER` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_REVIEW_BOOK` FOREIGN KEY (`BOOK_ID`) REFERENCES `BOOK` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `TRANSACTION` (
  `ID` varchar(36) NOT NULL,
  `QUANTITY` bigint NOT NULL DEFAULT 0,
  `USER_ID` varchar(36) NOT NULL,
  `BOOK_ID` varchar(36) NOT NULL,
  `BORROW_DATE` varchar(50) NOT NULL,
  `DUE_DATE` varchar(50) NOT NULL,
  `RETURN_DATE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TRANSACTION_USER` (`USER_ID`),
  KEY `FK_TRANSACTION_BOOK` (`BOOK_ID`),
  CONSTRAINT `FK_TRANSACTION_USER` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_TRANSACTION_BOOK` FOREIGN KEY (`BOOK_ID`) REFERENCES `BOOK` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `RETURN_DETAIL` (
  `ID` varchar(36) NOT NULL,
  `DAMAGED_QUANTITY` bigint NOT NULL DEFAULT 0,
  `PENALTY_FEE` bigint NOT NULL DEFAULT 0,
  `TRANSACTION_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TRANSACTION_RETURN_DETAIL` (`TRANSACTION_ID`),
  CONSTRAINT `FK_TRANSACTION_RETURN_DETAIL` FOREIGN KEY (`TRANSACTION_ID`) REFERENCES `TRANSACTION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `RESERVATION` (
  `ID` varchar(36) NOT NULL,
  `QUANTITY` bigint NOT NULL DEFAULT 0,
  `USER_ID` varchar(36) NOT NULL,
  `BOOK_ID` varchar(36) NOT NULL,
  `RESERVATION_DATE` varchar(50) NOT NULL,
  `EXPIRATION_DATE` varchar(50) NOT NULL,
  `STATUS` varchar(50) NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (`ID`),
  KEY `FK_RESERVATION_USER` (`USER_ID`),
  KEY `FK_RESERVATION_BOOK` (`BOOK_ID`),
  CONSTRAINT `FK_RESERVATION_USER` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_RESERVATION_BOOK` FOREIGN KEY (`BOOK_ID`) REFERENCES `BOOK` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
-- 1. Insert Categories
INSERT INTO `CATEGORY` (`ID`, `NAME`, `DESCRIPTION`) VALUES
('c1', 'Chính trị', 'Sách về chính trị, xã hội và tư tưởng'),
('c2', 'Programming', 'Books on programming and software development'),
('c3', 'Frontend', 'Books on frontend development and web design'),
('c4', 'Backend', 'Books on backend development and server-side programming'),
('c5', 'UI/UX', 'Books on user interface and experience design');

-- 2. Insert Publishers
INSERT INTO `PUBLISHER` (`ID`, `NAME`, `ADDRESS`, `EMAIL`, `PHONE`) VALUES
('p1', 'NXB Chính trị Quốc gia Sự thật', 'Hà Nội, Việt Nam', 'contact@nxbctqg.org.vn', '024.37472101'),
('p2', 'O\'Reilly Media', 'Sebastopol, CA, USA', 'info@oreilly.com', '707-827-7000'),
('p3', 'Manning Publications', 'Shelter Island, NY, USA', 'info@manning.com', '917-499-5864'),
('p4', 'Addison-Wesley', 'Boston, MA, USA', 'contact@pearson.com', '617-848-6000'),
('p5', 'No Starch Press', 'San Francisco, CA, USA', 'info@nostarch.com', '415-863-9900'),
('p6', 'Wiley', 'Hoboken, NJ, USA', 'info@wiley.com', '201-748-6000');

-- 3. Insert Authors
INSERT INTO `AUTHOR` (`ID`, `NAME`, `BIOGRAPHY`) VALUES
('a1', 'TS. Lê Minh Toàn', 'Tiến sĩ Luật học, giảng viên đại học tại Việt Nam'),
('a2', 'PGS.TS. Phạm Văn Đức', 'Phó Giáo sư, Tiến sĩ, chuyên gia về triết học Mác - Lênin'),
('a3', 'Bộ Giáo dục và Đào tạo', 'Cơ quan quản lý nhà nước về giáo dục tại Việt Nam'),
('a4', 'Robert C. Martin', 'Software engineer and author, known for promoting agile software development and software craftsmanship'),
('a5', 'Kyle Simpson', 'JavaScript developer, author of the "You Don\'t Know JS" book series'),
('a6', 'Erich Gamma', 'Software engineer, one of the Gang of Four authors'),
('a7', 'Richard Helm', 'Software engineer, one of the Gang of Four authors'),
('a8', 'Ralph Johnson', 'Software engineer, one of the Gang of Four authors'),
('a9', 'John Vlissides', 'Software engineer, one of the "Gang of Four" authors'),
('a10', 'Douglas Crockford', 'Creator of JSON and JavaScript expert'),
('a11', 'Steve Krug', 'Usability expert and author'),
('a12', 'Andrew Hunt', 'Co-founder of the Pragmatic Programmers'),
('a13', 'David Thomas', 'Co-founder of the Pragmatic Programmers'),
('a14', 'Martin Fowler', 'Software engineer and author specializing in software design'),
('a15', 'Marijn Haverbeke', 'JavaScript programmer and author'),
('a16', 'Jon Duckett', 'Author of web development books'),
('a17', 'Alex Banks', 'Software engineer and author'),
('a18', 'Eve Porcello', 'Software engineer and author'),
('a19', 'Aditya Bhargava', 'Software engineer and author'),
('a20', 'Martin Kleppmann', 'Software engineer and researcher'),
('a21', 'Joel Marsh', 'UX designer and author'),
('a22', 'Eric Freeman', 'Author and software engineer'),
('a23', 'Elisabeth Robson', 'Author and software engineer'),
('a24', 'Luciano Ramalho', 'Python developer and author'),
('a25', 'Rex Hartson', 'Professor of Computer Science and author on UX'),
('a26', 'Pardha Pyla', 'Professor and author on UX'),
('a27', 'Craig Walls', 'Spring Framework specialist and author'),
('a28', 'Dmitry Jemerov', 'Kotlin developer and author'),
('a29', 'Svetlana Isakova', 'Kotlin developer and author'),
('a30', 'Lea Verou', 'Web developer, speaker, and author'),
('a31', 'Eric Matthes', 'Author and Python teacher');

-- 4. Insert Books
INSERT INTO `BOOK` (`ID`, `IMAGE_URL`, `TITLE`, `ISBN`, `RATING_COUNT`, `RATING`, `PUBLISHED_DATE`, `PUBLISHER_ID`) VALUES
('b1', 'https://www.nxbctqg.org.vn/img_data/images/709508658395_169686323_466117254736983_3540351537507976746_n.jpg', 'Pháp luật đại cương', '978-604-57-5825-8', 100, 4.3, '2020', 'p1'),
('b2', 'https://nxbctqg.org.vn/img_data/images/773528504979_mllkc.jpg', 'Giáo trình triết học Mác - Lê Nin', '978-604-57-5826-5', 50, 1.5, '2021', 'p1'),
('b3', 'https://nxbctqg.org.vn/img_data/images/316029535454_KHONG-CHUYEN.jpg', 'Tư tưởng Hồ Chí Minh', '978-604-57-5827-2', 10, 1.2, '2021', 'p1'),
('b4', 'https://nxbctqg.org.vn/img_data/images/036272640018_b1.jpg', 'Kinh tế chính trị Mác Lê Nin', '978-604-57-5828-9', 140, 5.0, '2021', 'p1'),
('b5', 'https://nxbctqg.org.vn/img_data/images/326523824123_b1.jpg', 'Chủ nghĩa xã hội khoa học', '978-604-57-5829-6', 41, 2.3, '2021', 'p1'),
('b6', 'https://m.media-amazon.com/images/I/41-sN-mzwKL.jpg', 'Clean Code', '978-0132350884', 1200, 8.0, '2008', 'p4'),
('b7', 'https://m.media-amazon.com/images/I/51O-cX8IcDL.jpg', 'You Don\'t Know JS: Scope & Closures', '978-1449335588', 10, 8.2, '2014', 'p2'),
('b8', 'https://m.media-amazon.com/images/I/51k5cA3Yv3L.jpg', 'Design Patterns', '978-0201633610', 1242, 9.0, '1994', 'p4'),
('b9', 'https://m.media-amazon.com/images/I/51gdDmGRc1L.jpg', 'JavaScript: The Good Parts', '978-0596517748', 12, 4.5, '2008', 'p2'),
('b10', 'https://m.media-amazon.com/images/I/41SH-SvWPxL.jpg', 'Don\'t Make Me Think', '978-0321965516', 10, 6.7, '2014', 'p5'),
('b11', 'https://m.media-amazon.com/images/I/41as+WafrFL.jpg', 'The Pragmatic Programmer', '978-0201616224', 41, 5.5, '1999', 'p4'),
('b12', 'https://m.media-amazon.com/images/I/51kLjsos7eL.jpg', 'Refactoring', '978-0134757599', 123, 4.3, '2018', 'p4'),
('b13', 'https://m.media-amazon.com/images/I/91asIC1fRwL.jpg', 'Eloquent JavaScript', '978-1593279509', 1222, 9.2, '2018', 'p5'),
('b14', 'https://m.media-amazon.com/images/I/41+eA4F7GPL.jpg', 'HTML and CSS: Design and Build Websites', '978-1118008188', 140, 3.3, '2011', 'p6'),
('b15', 'https://m.media-amazon.com/images/I/51JpH5yPffL.jpg', 'Learning React', '978-1492051725', 1432, 2.3, '2020', 'p2'),
('b16', 'https://m.media-amazon.com/images/I/61u6oaU6oFL.jpg', 'Grokking Algorithms', '978-1617292231', 14, 2.3, '2016', 'p3'),
('b17', 'https://m.media-amazon.com/images/I/41D5rfQnQBL.jpg', 'Designing Data-Intensive Applications', '978-1449373320', 15, 6.6, '2017', 'p2'),
('b18', 'https://m.media-amazon.com/images/I/71GucMfsX2L.jpg', 'UX for Beginners', '978-1491912683', 756, 7.6, '2015', 'p2'),
('b19', 'https://m.media-amazon.com/images/I/81vjDV8Z2hL.jpg', 'Head First Design Patterns', '978-0596007126', 45654, 5.4, '2004', 'p2'),
('b20', 'https://m.media-amazon.com/images/I/51cUVaBWZ0L.jpg', 'Fluent Python', '978-1491946008', 3456, 3.4, '2015', 'p2'),
('b21', 'https://m.media-amazon.com/images/I/41cKAY88zSL.jpg', 'The UX Book', '978-0123852410', 234, 6.6, '2012', 'p4'),
('b22', 'https://m.media-amazon.com/images/I/51Fg0sbL6BL.jpg', 'Spring in Action', '978-1617294945', 1234, 5.4, '2018', 'p3'),
('b23', 'https://m.media-amazon.com/images/I/51fgdlGZlnL.jpg', 'Kotlin in Action', '978-1617293290', 43, 5.5, '2017', 'p3'),
('b24', 'https://m.media-amazon.com/images/I/41x6Ofq71dL.jpg', 'CSS Secrets', '978-1449372637', 12, 8.9, '2015', 'p2'),
('b25', 'https://m.media-amazon.com/images/I/51Fkt1hdOUL.jpg', 'Python Crash Course', '978-1593276034', 521, 4.3, '2015', 'p5');

-- 5. Insert Book-Author relationships
INSERT INTO `BOOK_AUTHOR` (`BOOK_ID`, `AUTHOR_ID`) VALUES
('b1', 'a1'),
('b2', 'a2'),
('b3', 'a3'),
('b4', 'a3'),
('b5', 'a3'),
('b6', 'a4'),
('b7', 'a5'),
('b8', 'a6'),
('b8', 'a7'),
('b8', 'a8'),
('b8', 'a9'),
('b9', 'a10'),
('b10', 'a11'),
('b11', 'a12'),
('b11', 'a13'),
('b12', 'a14'),
('b13', 'a15'),
('b14', 'a16'),
('b15', 'a17'),
('b15', 'a18'),
('b16', 'a19'),
('b17', 'a20'),
('b18', 'a21'),
('b19', 'a22'),
('b19', 'a23'),
('b20', 'a24'),
('b21', 'a25'),
('b21', 'a26'),
('b22', 'a27'),
('b23', 'a28'),
('b23', 'a29'),
('b24', 'a30'),
('b25', 'a31');

-- 6. Insert Book-Category relationships
INSERT INTO `BOOK_CATEGORY` (`BOOK_ID`, `CATEGORY_ID`) VALUES
('b1', 'c1'),
('b2', 'c1'),
('b3', 'c1'),
('b4', 'c1'),
('b5', 'c1'),
('b6', 'c2'),
('b6', 'c4'),
('b7', 'c2'),
('b7', 'c3'),
('b8', 'c2'),
('b9', 'c2'),
('b9', 'c3'),
('b10', 'c5'),
('b11', 'c2'),
('b11', 'c4'),
('b12', 'c2'),
('b13', 'c3'),
('b14', 'c3'),
('b15', 'c3'),
('b16', 'c4'),
('b17', 'c4'),
('b18', 'c5'),
('b19', 'c2'),
('b20', 'c4'),
('b21', 'c5'),
('b22', 'c4'),
('b23', 'c4'),
('b24', 'c3'),
('b25', 'c4');

-- 7. Insert Users
INSERT INTO `USER` (`ID`, `NAME`, `CCCD`, `DOB`, `EMAIL`, `PASSWORD`, `ROLE`) VALUES
('u1', 'Admin', '012345678901', '2005-12-22', 'admin@library.com', '$2a$10$3zTioNki9RCK4G7g3MUO9eg2q2rUApe2Usrqx9rIKrW9e0XcmYKSe', 'ADMIN'),
('u2', 'John Doe', '012345678902', '2001-02-12', 'john@example.com', '$2a$10$3zTioNki9RCK4G7g3MUO9eg2q2rUApe2Usrqx9rIKrW9e0XcmYKSe', 'USER'),
('u3', 'Jane Smith', '012345678903', '2000-01-01', 'jane@example.com', '$2a$10$3zTioNki9RCK4G7g3MUO9eg2q2rUApe2Usrqx9rIKrW9e0XcmYKSe', 'USER'),
('u4', 'Bob Johnson', '012345678904', '1998-05-11', 'bob@example.com', '$2a$10$3zTioNki9RCK4G7g3MUO9eg2q2rUApe2Usrqx9rIKrW9e0XcmYKSe', 'USER'),
('u5', 'Alice Williams', '012345678905', '2003-09-14', 'alice@example.com', '$2a$10$3zTioNki9RCK4G7g3MUO9eg2q2rUApe2Usrqx9rIKrW9e0XcmYKSe', 'USER'),
('u6', 'Michael Brown', '012345678906', '2005-09-14', 'michael@example.com', '$2a$10$3zTioNki9RCK4G7g3MUO9eg2q2rUApe2Usrqx9rIKrW9e0XcmYKSe', 'USER'),
('u7', 'Emily Davis', '012345678907', '2008-11-25', 'emily@example.com', '$2a$10$3zTioNki9RCK4G7g3MUO9eg2q2rUApe2Usrqx9rIKrW9e0XcmYKSe', 'USER');

-- 8. Insert Inventory
INSERT INTO `INVENTORY` (`BOOK_ID`, `QUANTITY`, `BORROWED`, `DAMAGED`) VALUES
('b1', 5, 2, 2),
('b2', 3, 1, 1),
('b3', 4, 0, 1),
('b4', 2, 1, 0),
('b5', 6, 0, 0),
('b6', 8, 3, 2),
('b7', 4, 0, 0),
('b8', 3, 0, 1),
('b9', 5, 1, 1),
('b10', 2, 2, 0),
('b11', 6, 0, 0),
('b12', 3, 1, 0),
('b13', 4, 2, 1),
('b14', 7, 0, 0),
('b15', 2, 1, 0),
('b16', 3, 0, 0),
('b17', 5, 1, 0),
('b18', 2, 0, 0),
('b19', 4, 0, 1),
('b20', 3, 0, 0),
('b21', 2, 0, 0),
('b22', 6, 0, 0),
('b23', 4, 0, 0),
('b24', 3, 3, 0),
('b25', 7, 2, 2);

-- 9. Insert Reviews
INSERT INTO `REVIEW` (`ID`, `USER_ID`, `BOOK_ID`, `STAR`, `COMMENT`, `DATE`) VALUES
('r1', 'u2', 'b6', 5, 'A must-read for software developers. Changed how I think about code.', '2023-01-15'),
('r2', 'u3', 'b6', 4, 'Great principles but some examples are outdated.', '2023-02-10'),
('r3', 'u4', 'b7', 5, 'Finally understood closures after reading this.', '2023-01-20'),
('r4', 'u5', 'b8', 5, 'Classic book on design patterns. Still relevant today.', '2023-03-05'),
('r5', 'u6', 'b9', 4, 'Great insights on JavaScript good practices.', '2023-02-15'),
('r6', 'u7', 'b10', 5, 'Changed how I think about UX design. Simple and effective.', '2023-01-10'),
('r7', 'u2', 'b13', 4, 'Excellent introduction to JavaScript programming.', '2023-03-15'),
('r8', 'u3', 'b15', 5, 'Best React book I\'ve read. Great examples.', '2023-02-20'),
('r9', 'u4', 'b16', 4, 'Makes algorithms accessible and fun to learn.', '2023-01-25'),
('r10', 'u5', 'b17', 5, 'Comprehensive guide to modern data systems.', '2023-03-10');

-- 10. Insert Transactions
INSERT INTO `TRANSACTION` (`ID`, `QUANTITY`, `USER_ID`, `BOOK_ID`, `BORROW_DATE`, `DUE_DATE`, `RETURN_DATE`) VALUES
('t1', 1, 'u2', 'b6', '2023-01-01', '2023-01-15', '2023-01-14'),
('t2', 3, 'u3', 'b7', '2023-01-05', '2023-01-19', '2023-01-18'),
('t3', 5,'u4', 'b8', '2023-01-10', '2023-01-24', '2023-01-20'),
('t4', 7, 'u5', 'b9', '2023-01-15', '2023-01-29', '2023-01-28'),
('t5', 9, 'u6', 'b10', '2023-01-20', '2023-02-03', '2023-02-01'),
('t6', 1, 'u7', 'b11', '2023-01-25', '2023-02-08', NULL),
('t7', 2, 'u2', 'b12', '2023-02-01', '2023-02-15', '2023-02-14'),
('t8', 2, 'u3', 'b13', '2023-02-05', '2023-02-19', '2023-02-17'),
('t9', 7, 'u4', 'b14', '2023-02-10', '2023-02-24', NULL),
('t10', 3, 'u5', 'b15', '2023-02-15', '2023-03-01', '2023-02-28'),
('t11', 3, 'u6', 'b16', '2023-02-20', '2023-03-06', '2023-03-05'),
('t12', 7, 'u7', 'b17', '2023-02-25', '2023-03-11', NULL),
('t13', 4, 'u2', 'b18', '2023-03-01', '2023-03-15', '2023-03-14'),
('t14', 4, 'u3', 'b19', '2023-03-05', '2023-03-19', '2023-03-18'),
('t15', 3, 'u4', 'b20', '2023-03-10', '2023-03-24', NULL);

-- 11. Insert Return detail
INSERT INTO `RETURN_DETAIL` (`ID`, `DAMAGED_QUANTITY`, `PENALTY_FEE`, `TRANSACTION_ID`) VALUES
('rt1', 1, 25000, 't1'),
('rt2', 2, 50000, 't2'),
('rt3', 0, 0, 't3'),
('rt4', 0, 0, 't4'),
('rt5', 0, 0, 't5'),
('rt7', 1, 25000, 't7'),
('rt8', 2, 50000, 't8'),
('rt10', 0, 0, 't9'),
('rt11', 3, 75000, 't11'),
('rt13', 1, 25000, 't13'),
('rt14', 2, 50000, 't14');

-- 12 Insert Reservations
INSERT INTO `RESERVATION` (`ID`, `QUANTITY`, `USER_ID`, `BOOK_ID`, `RESERVATION_DATE`, `EXPIRATION_DATE`, `STATUS`) VALUES
('res1', 2, 'u2', 'b21', '2023-03-15', '2023-03-22', 'COMPLETED'),
('res2', 5, 'u3', 'b22', '2023-03-16', '2023-03-23', 'COMPLETED'),
('res3', 1, 'u4', 'b23', '2023-03-17', '2023-03-24', 'PENDING'),
('res4', 5, 'u5', 'b24', '2023-03-18', '2023-03-25', 'PENDING'),
('res5', 6, 'u6', 'b25', '2023-03-19', '2023-03-26', 'CANCELLED'),
('res6', 1, 'u7', 'b1', '2023-03-20', '2023-03-27', 'PENDING'),
('res7', 2, 'u2', 'b2', '2023-03-21', '2023-03-28', 'PENDING');