SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `nextjslibrarydatabase` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `nextjslibrarydatabase`;

-- Clear existing tables if they exist
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `RESERVATION`;
DROP TABLE IF EXISTS `TRANSACTION`;
DROP TABLE IF EXISTS `TRANSACTION_DETAIL`;
DROP TABLE IF EXISTS `REVIEW`;
DROP TABLE IF EXISTS `BOOK_COPY`;
DROP TABLE IF EXISTS `BOOK_TITLE`;
DROP TABLE IF EXISTS `BOOK_CATEGORY`;
DROP TABLE IF EXISTS `BOOK_AUTHOR`;
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
  `BIRTHDAY` date DEFAULT NULL,
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
  `ADDRESS` text NOT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `PHONE` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `USER` (
  `ID` varchar(36) NOT NULL,
  `CCCD` char(15) NOT NULL,
  `DOB` date NOT NULL,
  `AVATAR_URL` text DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `PASSWORD` varchar(255),
  `ROLE` varchar(50) DEFAULT 'USER',
  `BALANCE` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `EMAIL_UNIQUE` (`EMAIL`),
  UNIQUE KEY `CCCD_UNIQUE` (`CCCD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `BOOK_TITLE` (
  `ID` varchar(36) NOT NULL,
  `IMAGE_URL` text DEFAULT NULL,
  `TITLE` varchar(255) NOT NULL,
  `ISBN` varchar(50) DEFAULT NULL,
  `CAN_BORROW` BOOLEAN NOT NULL DEFAULT TRUE,
  `PRICE` INT NOT NULL DEFAULT 0,
  `PUBLISHED_DATE` date DEFAULT NULL,
  `PUBLISHER_ID` varchar(36) DEFAULT NULL,
  `TOTAL_COPIES` INT NOT NULL DEFAULT 0,
  `MAX_ONLINE_RESERVATIONS` INT NOT NULL DEFAULT 0,
  UNIQUE KEY `UK_ISBN` (`ISBN`),
  PRIMARY KEY (`ID`),
  KEY `FK_BOOK_TITLE_PUBLISHER` (`PUBLISHER_ID`),
  CONSTRAINT `FK_BOOK_TITLE_PUBLISHER` FOREIGN KEY (`PUBLISHER_ID`) REFERENCES `PUBLISHER` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `BOOK_COPY` (
  `ID` varchar(36) NOT NULL,
  `BOOK_TITLE_ID` varchar(36) NOT NULL,
  `STATUS` varchar(50) NOT NULL DEFAULT 'AVAILABLE',
  `CONDITION` varchar(20) NOT NULL DEFAULT 'NEW',
  PRIMARY KEY (`ID`),
  KEY `FK_BOOK_COPY_BOOK_TITLE` (`BOOK_TITLE_ID`),
  CONSTRAINT `FK_BOOK_COPY_BOOK_TITLE` FOREIGN KEY (`BOOK_TITLE_ID`) REFERENCES `BOOK_TITLE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `BOOK_AUTHOR` (
  `BOOK_TITLE_ID` varchar(36) NOT NULL,
  `AUTHOR_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`BOOK_TITLE_ID`, `AUTHOR_ID`),
  KEY `FK_BA_AUTHOR` (`AUTHOR_ID`),
  CONSTRAINT `FK_BA_BOOK_TITLE` FOREIGN KEY (`BOOK_TITLE_ID`) REFERENCES `BOOK_TITLE` (`ID`),
  CONSTRAINT `FK_BA_AUTHOR` FOREIGN KEY (`AUTHOR_ID`) REFERENCES `AUTHOR` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `BOOK_CATEGORY` (
  `BOOK_TITLE_ID` varchar(36) NOT NULL,
  `CATEGORY_ID` varchar(36) NOT NULL,
  PRIMARY KEY (`BOOK_TITLE_ID`, `CATEGORY_ID`),
  KEY `FK_BC_CATEGORY` (`CATEGORY_ID`),
  CONSTRAINT `FK_BC_BOOK_TITLE` FOREIGN KEY (`BOOK_TITLE_ID`) REFERENCES `BOOK_TITLE` (`ID`),
  CONSTRAINT `FK_BC_CATEGORY` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `CATEGORY` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `REVIEW` (
  `ID` varchar(36) NOT NULL,
  `USER_ID` varchar(36) NOT NULL,
  `BOOK_TITLE_ID` varchar(36) NOT NULL,
  `STAR` int(11) NOT NULL,
  `COMMENT` text DEFAULT NULL,
  `DATE` date NOT NULL ,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_USER_BOOK_TITLE` (`USER_ID`, `BOOK_TITLE_ID`),
  KEY `FK_REVIEW_BOOK_TITLE` (`BOOK_TITLE_ID`),
  CONSTRAINT `FK_REVIEW_USER` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_REVIEW_BOOK_TITLE` FOREIGN KEY (`BOOK_TITLE_ID`) REFERENCES `BOOK_TITLE` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `TRANSACTION` (
  `ID` varchar(36) NOT NULL,
  `USER_ID` varchar(36) NOT NULL,
  `BORROW_DATE` date NOT NULL,
  `DUE_DATE` date NOT NULL,
  `BOOK_COPY_ID` varchar(36) NOT NULL,
  `RETURNED_DATE` date DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TRANSACTION_USER` (`USER_ID`),
  KEY `FK_TRANSACTION_BOOK_COPY` (`BOOK_COPY_ID`),
  CONSTRAINT `FK_TRANSACTION_USER` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_TRANSACTION_BOOK_COPY` FOREIGN KEY (`BOOK_COPY_ID`) REFERENCES `BOOK_COPY` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `TRANSACTION_DETAIL` (
  `TRANSACTION_ID` varchar(36) NOT NULL,
  `PENALTY_FEE` bigint NOT NULL DEFAULT 0,
  `DESCRIPTION` text DEFAULT NULL,
  PRIMARY KEY (`TRANSACTION_ID`),
  KEY `FK_TRANSACTION_DETAIL_TRANSACTION` (`TRANSACTION_ID`),
  CONSTRAINT `FK_TRANSACTION_DETAIL_TRANSACTION` FOREIGN KEY (`TRANSACTION_ID`) REFERENCES `TRANSACTION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `RESERVATION` (
  `ID` varchar(36) NOT NULL,
  `USER_ID` varchar(36) NOT NULL,
  `BOOK_TITLE_ID` varchar(36) NOT NULL,
  `BOOK_COPY_ID` varchar(36) DEFAULT NULL,
  `RESERVATION_DATE` date NOT NULL,
  `EXPIRATION_DATE` date NOT NULL,
  `DEPOSIT` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`ID`),
  KEY `FK_RESERVATION_USER` (`USER_ID`),
  KEY `FK_RESERVATION_BOOK_TITLE` (`BOOK_TITLE_ID`),
  KEY `FK_RESERVATION_BOOK_COPY` (`BOOK_COPY_ID`),
  CONSTRAINT `FK_RESERVATION_USER` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`ID`),
  CONSTRAINT `FK_RESERVATION_BOOK_TITLE` FOREIGN KEY (`BOOK_TITLE_ID`) REFERENCES `BOOK_TITLE` (`ID`),
  CONSTRAINT `FK_RESERVATION_BOOK_COPY` FOREIGN KEY (`BOOK_COPY_ID`) REFERENCES `BOOK_COPY` (`ID`)
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
INSERT INTO `AUTHOR` (`ID`, `NAME`, `BIRTHDAY`, `BIOGRAPHY`) VALUES
('a1', 'TS. Lê Minh Toàn', '1975-03-15', 'Tiến sĩ Luật học, giảng viên đại học tại Việt Nam'),
('a2', 'PGS.TS. Phạm Văn Đức', '1970-08-22', 'Phó Giáo sư, Tiến sĩ, chuyên gia về triết học Mác - Lênin'),
('a3', 'Bộ Giáo dục và Đào tạo', '1945-09-02', 'Cơ quan quản lý nhà nước về giáo dục tại Việt Nam'),
('a4', 'Robert C. Martin', '1952-12-05', 'Software engineer and author, known for promoting agile software development and software craftsmanship'),
('a5', 'Kyle Simpson', '1979-07-18', 'JavaScript developer, author of the "You Don\'t Know JS" book series'),
('a6', 'Erich Gamma', '1961-03-13', 'Software engineer, one of the Gang of Four authors'),
('a7', 'Richard Helm', '1956-11-27', 'Software engineer, one of the Gang of Four authors'),
('a8', 'Ralph Johnson', '1955-06-30', 'Software engineer, one of the Gang of Four authors'),
('a9', 'John Vlissides', '1961-08-02', 'Software engineer, one of the "Gang of Four" authors'),
('a10', 'Douglas Crockford', '1955-02-12', 'Creator of JSON and JavaScript expert'),
('a11', 'Steve Krug', '1950-04-25', 'Usability expert and author'),
('a12', 'Andrew Hunt', '1964-10-14', 'Co-founder of the Pragmatic Programmers'),
('a13', 'David Thomas', '1956-01-19', 'Co-founder of the Pragmatic Programmers'),
('a14', 'Martin Fowler', '1963-12-18', 'Software engineer and author specializing in software design'),
('a15', 'Marijn Haverbeke', '1989-05-03', 'JavaScript programmer and author'),
('a16', 'Jon Duckett', '1971-09-12', 'Author of web development books'),
('a17', 'Alex Banks', '1983-11-08', 'Software engineer and author'),
('a18', 'Eve Porcello', '1985-07-24', 'Software engineer and author'),
('a19', 'Aditya Bhargava', '1987-02-16', 'Software engineer and author'),
('a20', 'Martin Kleppmann', '1981-04-09', 'Software engineer and researcher'),
('a21', 'Joel Marsh', '1978-12-31', 'UX designer and author'),
('a22', 'Eric Freeman', '1965-06-15', 'Author and software engineer'),
('a23', 'Elisabeth Robson', '1968-03-20', 'Author and software engineer'),
('a24', 'Luciano Ramalho', '1960-11-07', 'Python developer and author'),
('a25', 'Rex Hartson', '1958-08-14', 'Professor of Computer Science and author on UX'),
('a26', 'Pardha Pyla', '1975-01-28', 'Professor and author on UX'),
('a27', 'Craig Walls', '1970-05-11', 'Spring Framework specialist and author'),
('a28', 'Dmitry Jemerov', '1982-09-03', 'Kotlin developer and author'),
('a29', 'Svetlana Isakova', '1985-12-19', 'Kotlin developer and author'),
('a30', 'Lea Verou', '1986-06-26', 'Web developer, speaker, and author'),
('a31', 'Eric Matthes', '1975-10-17', 'Author and Python teacher');

-- 4. Insert BookTitles
INSERT INTO `BOOK_TITLE` (`ID`, `IMAGE_URL`, `TITLE`, `ISBN`, `PRICE`, `PUBLISHED_DATE`, `PUBLISHER_ID`, `TOTAL_COPIES`, `MAX_ONLINE_RESERVATIONS`) VALUES
('b1', 'https://www.nxbctqg.org.vn/img_data/images/709508658395_169686323_466117254736983_3540351537507976746_n.jpg', 'Pháp luật đại cương', '978-604-57-5825-8', 85000, '2020-01-01', 'p1', 3, 2),
('b2', 'https://nxbctqg.org.vn/img_data/images/773528504979_mllkc.jpg', 'Giáo trình triết học Mác - Lê Nin', '978-604-57-5826-5', 95000, '2020-01-01', 'p1', 3, 2),
('b3', 'https://nxbctqg.org.vn/img_data/images/316029535454_KHONG-CHUYEN.jpg', 'Tư tưởng Hồ Chí Minh', '978-604-57-5827-2', 75000, '1999-09-12', 'p1', 3, 2),
('b4', 'https://nxbctqg.org.vn/img_data/images/036272640018_b1.jpg', 'Kinh tế chính trị Mác Lê Nin', '978-604-57-5828-9', 88000, '1989-02-01', 'p1', 3, 2),
('b5', 'https://nxbctqg.org.vn/img_data/images/326523824123_b1.jpg', 'Chủ nghĩa xã hội khoa học', '978-604-57-5829-6', 82000, '2005-02-22', 'p1', 3, 2),
('b6', 'https://m.media-amazon.com/images/I/41-sN-mzwKL.jpg', 'Clean Code', '978-0132350884', 450000, '1999-01-10', 'p4', 5, 3),
('b7', 'https://m.media-amazon.com/images/I/51O-cX8IcDL.jpg', 'You Don\'t Know JS: Scope & Closures', '978-1449335588', 320000, '2020-12-01', 'p2', 4, 3),
('b8', 'https://m.media-amazon.com/images/I/51k5cA3Yv3L.jpg', 'Design Patterns', '978-0201633610', 580000, '2022-09-14', 'p4', 4, 3),
('b9', 'https://m.media-amazon.com/images/I/51gdDmGRc1L.jpg', 'JavaScript: The Good Parts', '978-0596517748', 295000, '1987-05-23', 'p2', 4, 3),
('b10', 'https://m.media-amazon.com/images/I/41SH-SvWPxL.jpg', 'Don\'t Make Me Think', '978-0321965516', 415000, '2020-01-14', 'p5', 3, 2),
('b11', 'https://m.media-amazon.com/images/I/41as+WafrFL.jpg', 'The Pragmatic Programmer', '978-0201616224', 495000, '1778-02-03', 'p4', 5, 4),
('b12', 'https://m.media-amazon.com/images/I/51kLjsos7eL.jpg', 'Refactoring', '978-0134757599', 540000, '2012-08-12', 'p4', 4, 3),
('b13', 'https://m.media-amazon.com/images/I/91asIC1fRwL.jpg', 'Eloquent JavaScript', '978-1593279509', 380000, '2020-08-05', 'p5', 4, 3),
('b14', 'https://m.media-amazon.com/images/I/41+eA4F7GPL.jpg', 'HTML and CSS: Design and Build Websites', '978-1118008188', 355000, '2020-07-22', 'p6', 3, 2),
('b15', 'https://m.media-amazon.com/images/I/51JpH5yPffL.jpg', 'Learning React', '978-1492051725', 435000, '1991-11-11', 'p2', 4, 3),
('b16', 'https://m.media-amazon.com/images/I/61u6oaU6oFL.jpg', 'Grokking Algorithms', '978-1617292231', 395000, '2023-06-20', 'p3', 3, 2),
('b17', 'https://m.media-amazon.com/images/I/41D5rfQnQBL.jpg', 'Designing Data-Intensive Applications', '978-1449373320', 620000, '1888-04-01', 'p2', 2, 1),
('b18', 'https://m.media-amazon.com/images/I/71GucMfsX2L.jpg', 'UX for Beginners', '978-1491912683', 310000, '2020-01-26', 'p2', 3, 2),
('b19', 'https://m.media-amazon.com/images/I/81vjDV8Z2hL.jpg', 'Head First Design Patterns', '978-0596007126', 485000, '1876-08-09', 'p2', 4, 3),
('b20', 'https://m.media-amazon.com/images/I/51cUVaBWZ0L.jpg', 'Fluent Python', '978-1491946008', 525000, '1882-12-01', 'p2', 3, 2),
('b21', 'https://m.media-amazon.com/images/I/41cKAY88zSL.jpg', 'The UX Book', '978-0123852410', 675000, '1881-12-30', 'p4', 2, 1),
('b22', 'https://m.media-amazon.com/images/I/51Fg0sbL6BL.jpg', 'Spring in Action', '978-1617294945', 465000, '1919-04-05', 'p3', 4, 3),
('b23', 'https://m.media-amazon.com/images/I/51fgdlGZlnL.jpg', 'Kotlin in Action', '978-1617293290', 410000, '2020-10-06', 'p3', 3, 2),
('b24', 'https://m.media-amazon.com/images/I/41x6Ofq71dL.jpg', 'CSS Secrets', '978-1449372637', 365000, '1993-01-21', 'p2', 3, 2),
('b25', 'https://m.media-amazon.com/images/I/51Fkt1hdOUL.jpg', 'Python Crash Course', '978-1593276034', 335000, '1999-01-01', 'p5', 4, 3);

-- 5. Insert Book Copies
INSERT INTO `BOOK_COPY` (`ID`, `BOOK_TITLE_ID`, `STATUS`, `CONDITION`) VALUES
('bc1-1',  'b1',  'BORROWED', 'GOOD'),
('bc1-2',  'b1',  'AVAILABLE', 'GOOD'),
('bc1-3',  'b1',  'AVAILABLE', 'NEW'),
('bc2-1',  'b2',  'BORROWED', 'GOOD'),
('bc2-2',  'b2',  'AVAILABLE', 'GOOD'),
('bc2-3',  'b2',  'AVAILABLE', 'NEW'),
('bc3-1',  'b3',  'BORROWED', 'GOOD'),
('bc3-2',  'b3',  'AVAILABLE', 'NEW'),
('bc3-3',  'b3',  'AVAILABLE', 'NEW'),
('bc4-1',  'b4',  'BORROWED', 'GOOD'),
('bc4-2',  'b4',  'AVAILABLE', 'NEW'),
('bc4-3',  'b4',  'AVAILABLE', 'NEW'),
('bc5-1',  'b5',  'BORROWED', 'GOOD'),
('bc5-2',  'b5',  'AVAILABLE', 'NEW'),
('bc5-3',  'b5',  'AVAILABLE', 'NEW'),
('bc6-1',  'b6',  'BORROWED', 'GOOD'),
('bc6-2',  'b6',  'AVAILABLE', 'NEW'),
('bc6-3',  'b6',  'AVAILABLE', 'NEW'),
('bc6-4',  'b6',  'AVAILABLE', 'NEW'),
('bc7-1',  'b7',  'BORROWED', 'GOOD'),
('bc7-2',  'b7',  'AVAILABLE', 'NEW'),
('bc7-3',  'b7',  'AVAILABLE', 'NEW'),
('bc7-4',  'b7',  'AVAILABLE', 'NEW'),
('bc8-1',  'b8',  'BORROWED', 'GOOD'),
('bc8-2',  'b8',  'AVAILABLE', 'NEW'),
('bc8-3',  'b8',  'AVAILABLE', 'NEW'),
('bc8-4',  'b8',  'AVAILABLE', 'NEW'),
('bc9-1',  'b9',  'BORROWED', 'GOOD'),
('bc9-2',  'b9',  'AVAILABLE', 'NEW'),
('bc9-3',  'b9',  'AVAILABLE', 'NEW'),
('bc9-4',  'b9',  'AVAILABLE', 'NEW'),
('bc10-1', 'b10', 'BORROWED', 'GOOD'),
('bc10-2', 'b10', 'AVAILABLE', 'NEW'),
('bc10-3', 'b10', 'AVAILABLE', 'NEW'),
('bc10-4', 'b10', 'AVAILABLE', 'NEW'),
('bc11-1', 'b11', 'BORROWED', 'GOOD'),
('bc11-2', 'b11', 'AVAILABLE', 'NEW'),
('bc11-3', 'b11', 'AVAILABLE', 'NEW'),
('bc11-4', 'b11', 'AVAILABLE', 'NEW'),
('bc12-1', 'b12', 'BORROWED', 'GOOD'),
('bc12-2', 'b12', 'AVAILABLE', 'NEW'),
('bc12-3', 'b12', 'AVAILABLE', 'NEW'),
('bc12-4', 'b12', 'AVAILABLE', 'NEW'),
('bc13-1', 'b13', 'BORROWED', 'GOOD'),
('bc13-2', 'b13', 'AVAILABLE', 'NEW'),
('bc13-3', 'b13', 'AVAILABLE', 'NEW'),
('bc13-4', 'b13', 'AVAILABLE', 'NEW'),
('bc14-1', 'b14', 'BORROWED', 'GOOD'),
('bc14-2', 'b14', 'AVAILABLE', 'NEW'),
('bc14-3', 'b14', 'AVAILABLE', 'NEW'),
('bc14-4', 'b14', 'AVAILABLE', 'NEW'),
('bc15-1', 'b15', 'BORROWED', 'GOOD'),
('bc15-2', 'b15', 'AVAILABLE', 'NEW'),
('bc15-3', 'b15', 'AVAILABLE', 'NEW'),
('bc15-4', 'b15', 'AVAILABLE', 'NEW'),
('bc16-1', 'b16', 'BORROWED', 'GOOD'),
('bc16-2', 'b16', 'AVAILABLE', 'NEW'),
('bc16-3', 'b16', 'AVAILABLE', 'NEW'),
('bc16-4', 'b16', 'AVAILABLE', 'NEW'),
('bc16-5', 'b16', 'AVAILABLE', 'NEW'),
('bc17-1', 'b17', 'AVAILABLE', 'NEW'),
('bc17-2', 'b17', 'AVAILABLE', 'NEW'),
('bc17-3', 'b17', 'AVAILABLE', 'NEW'),
('bc17-4', 'b17', 'AVAILABLE', 'NEW'),
('bc17-5', 'b17', 'AVAILABLE', 'NEW'),
('bc18-1', 'b18', 'AVAILABLE', 'NEW'),
('bc18-2', 'b18', 'AVAILABLE', 'NEW'),
('bc18-3', 'b18', 'AVAILABLE', 'NEW'),
('bc18-4', 'b18', 'AVAILABLE', 'NEW'),
('bc18-5', 'b18', 'AVAILABLE', 'NEW'),
('bc19-1', 'b19', 'AVAILABLE', 'NEW'),
('bc19-2', 'b19', 'AVAILABLE', 'NEW'),
('bc19-3', 'b19', 'AVAILABLE', 'NEW'),
('bc19-4', 'b19', 'AVAILABLE', 'NEW'),
('bc19-5', 'b19', 'AVAILABLE', 'NEW'),
('bc20-1', 'b20', 'AVAILABLE', 'NEW'),
('bc20-2', 'b20', 'AVAILABLE', 'NEW'),
('bc20-3', 'b20', 'AVAILABLE', 'NEW'),
('bc20-4', 'b20', 'AVAILABLE', 'NEW'),
('bc20-5', 'b20', 'AVAILABLE', 'NEW'),
('bc21-1', 'b21', 'AVAILABLE', 'NEW'),
('bc21-2', 'b21', 'AVAILABLE', 'NEW'),
('bc21-3', 'b21', 'AVAILABLE', 'NEW'),
('bc21-4', 'b21', 'AVAILABLE', 'NEW'),
('bc21-5', 'b21', 'AVAILABLE', 'NEW'),
('bc22-1', 'b22', 'AVAILABLE', 'NEW'),
('bc22-2', 'b22', 'AVAILABLE', 'NEW'),
('bc22-3', 'b22', 'AVAILABLE', 'NEW'),
('bc22-4', 'b22', 'AVAILABLE', 'NEW'),
('bc22-5', 'b22', 'AVAILABLE', 'NEW'),
('bc23-1', 'b23', 'AVAILABLE', 'NEW'),
('bc23-2', 'b23', 'AVAILABLE', 'NEW'),
('bc23-3', 'b23', 'AVAILABLE', 'NEW'),
('bc23-4', 'b23', 'AVAILABLE', 'NEW'),
('bc23-5', 'b23', 'AVAILABLE', 'NEW'),
('bc24-1', 'b24', 'AVAILABLE', 'NEW'),
('bc24-2', 'b24', 'AVAILABLE', 'NEW'),
('bc24-3', 'b24', 'AVAILABLE', 'NEW'),
('bc24-4', 'b24', 'AVAILABLE', 'WORN'),
('bc24-5', 'b24', 'LOST', 'DAMAGED'),
('bc25-1', 'b25', 'AVAILABLE', 'NEW'),
('bc25-2', 'b25', 'AVAILABLE', 'GOOD'),
('bc25-3', 'b25', 'AVAILABLE', 'WORN'),
('bc25-4', 'b25', 'AVAILABLE', 'DAMAGED'),
('bc25-5', 'b25', 'AVAILABLE', 'NEW');

-- 6. Insert Book-Author relationships
INSERT INTO `BOOK_AUTHOR` (`BOOK_TITLE_ID`, `AUTHOR_ID`) VALUES
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

-- 7. Insert Book-Category relationships
INSERT INTO `BOOK_CATEGORY` (`BOOK_TITLE_ID`, `CATEGORY_ID`) VALUES
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

-- 8. Insert Users
INSERT INTO `USER` (`ID`, `NAME`, `CCCD`, `DOB`, `EMAIL`, `PASSWORD`, `ROLE`, `BALANCE`)VALUES
-- password is password
('u1', 'Admin', '012345678901', '2005-12-22', 'admin@library.com', '$2b$12$glHMHujIg84/uCQ/8Myrm.r6Dp3.RTh60BJlyDrSYowXsl5/Sc5gK', 'ADMIN', 0),
('u2', 'John Doe', '012345678902', '2001-02-12', 'john@example.com', '$2b$12$glHMHujIg84/uCQ/8Myrm.r6Dp3.RTh60BJlyDrSYowXsl5/Sc5gK', 'LIBRARIAN', 0),
('u3', 'Jane Smith', '012345678903', '2000-01-01', 'jane@example.com', '$2b$12$glHMHujIg84/uCQ/8Myrm.r6Dp3.RTh60BJlyDrSYowXsl5/Sc5gK', 'USER', 3500000),
('u4', 'Bob Johnson', '012345678904', '1998-05-11', 'bob@example.com', '$2b$12$glHMHujIg84/uCQ/8Myrm.r6Dp3.RTh60BJlyDrSYowXsl5/Sc5gK', 'USER', 7200000),
('u5', 'Alice Williams', '012345678905', '2003-09-14', 'alice@example.com', '$2b$12$glHMHujIg84/uCQ/8Myrm.r6Dp3.RTh60BJlyDrSYowXsl5/Sc5gK', 'USER', 1800000),
('u6', 'Michael Brown', '012345678906', '2005-09-14', 'michael@example.com', '$2b$12$glHMHujIg84/uCQ/8Myrm.r6Dp3.RTh60BJlyDrSYowXsl5/Sc5gK', 'USER', 5500000),
('u7', 'Emily Davis', '012345678907', '2008-11-25', 'emily@example.com', '$2b$12$glHMHujIg84/uCQ/8Myrm.r6Dp3.RTh60BJlyDrSYowXsl5/Sc5gK', 'USER', 2750000);

-- 9. Insert Reviews
INSERT INTO `REVIEW` (`ID`, `USER_ID`, `BOOK_TITLE_ID`, `STAR`, `COMMENT`, `DATE`) VALUES
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

-- 10. Insert Transactions (now with BOOK_COPY_ID and RETURNED_DATE)
INSERT INTO `TRANSACTION` (`ID`, `USER_ID`, `BORROW_DATE`, `DUE_DATE`, `BOOK_COPY_ID`, `RETURNED_DATE`) VALUES
('t1', 'u2', '2023-01-01', '2023-01-15', 'bc1-1', NULL),
('t2', 'u3', '2023-01-05', '2023-01-19', 'bc3-1', NULL),
('t3', 'u4', '2023-01-10', '2023-01-24', 'bc5-1', NULL),
('t4', 'u5', '2023-01-15', '2023-01-29', 'bc7-1', NULL),
('t5', 'u6', '2023-01-20', '2023-02-03', 'bc9-1', NULL),
('t6', 'u7', '2023-01-25', '2023-02-08', 'bc11-1', NULL),
('t7', 'u2', '2023-02-01', '2023-02-15', 'bc13-1', NULL),
('t8', 'u3', '2023-02-05', '2023-02-19', 'bc15-1', NULL);

-- 11. Insert Transaction Details (only for damaged/lost/penalty, example row)
-- Example: t1 had a penalty
INSERT INTO `TRANSACTION_DETAIL` (`TRANSACTION_ID`, `PENALTY_FEE`, `DESCRIPTION`) VALUES
('t1', 50000, 'Book returned with torn pages');

-- 12 Insert Reservations
INSERT INTO `RESERVATION` (`ID`, `USER_ID`, `BOOK_TITLE_ID`, `BOOK_COPY_ID`, `RESERVATION_DATE`, `EXPIRATION_DATE`, `DEPOSIT`) VALUES
('res1', 'u2', 'b21', 'bc21-2', '2023-03-15', '2023-03-22', 45000),
('res2', 'u3', 'b22', 'bc22-2', '2023-03-16', '2023-03-23', 42000),
('res3', 'u4', 'b23', NULL, '2023-03-17', '2023-03-24', 41000),
('res4', 'u5', 'b24', NULL, '2023-03-18', '2023-03-25', 36500),
('res5', 'u6', 'b25', 'bc25-2', '2023-03-19', '2023-03-26', 33500),
('res6', 'u7', 'b1', NULL, '2023-03-20', '2023-03-27', 50000),
('res7', 'u2', 'b2', NULL, '2023-03-21', '2023-03-28', 42000);





-- ==================================================================
-- Below is the Quartz initialization script, which is not part of the database schema
-- You must not change this part
-- Script: https://github.com/quartz-scheduler/quartz/blob/main/quartz/src/main/resources/org/quartz/impl/jdbcjobstore/tables_mysql_innodb.sql
-- ==================================================================

DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

CREATE TABLE QRTZ_JOB_DETAILS(
                                 SCHED_NAME VARCHAR(120) NOT NULL,
                                 JOB_NAME VARCHAR(190) NOT NULL,
                                 JOB_GROUP VARCHAR(190) NOT NULL,
                                 DESCRIPTION VARCHAR(250) NULL,
                                 JOB_CLASS_NAME VARCHAR(250) NOT NULL,
                                 IS_DURABLE VARCHAR(1) NOT NULL,
                                 IS_NONCONCURRENT VARCHAR(1) NOT NULL,
                                 IS_UPDATE_DATA VARCHAR(1) NOT NULL,
                                 REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
                                 JOB_DATA BLOB NULL,
                                 PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_TRIGGERS (
                               SCHED_NAME VARCHAR(120) NOT NULL,
                               TRIGGER_NAME VARCHAR(190) NOT NULL,
                               TRIGGER_GROUP VARCHAR(190) NOT NULL,
                               JOB_NAME VARCHAR(190) NOT NULL,
                               JOB_GROUP VARCHAR(190) NOT NULL,
                               DESCRIPTION VARCHAR(250) NULL,
                               NEXT_FIRE_TIME BIGINT(13) NULL,
                               PREV_FIRE_TIME BIGINT(13) NULL,
                               PRIORITY INTEGER NULL,
                               TRIGGER_STATE VARCHAR(16) NOT NULL,
                               TRIGGER_TYPE VARCHAR(8) NOT NULL,
                               START_TIME BIGINT(13) NOT NULL,
                               END_TIME BIGINT(13) NULL,
                               CALENDAR_NAME VARCHAR(190) NULL,
                               MISFIRE_INSTR SMALLINT(2) NULL,
                               JOB_DATA BLOB NULL,
                               PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                               FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
                                   REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
                                      SCHED_NAME VARCHAR(120) NOT NULL,
                                      TRIGGER_NAME VARCHAR(190) NOT NULL,
                                      TRIGGER_GROUP VARCHAR(190) NOT NULL,
                                      REPEAT_COUNT BIGINT(7) NOT NULL,
                                      REPEAT_INTERVAL BIGINT(12) NOT NULL,
                                      TIMES_TRIGGERED BIGINT(10) NOT NULL,
                                      PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                      FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
                                          REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_CRON_TRIGGERS (
                                    SCHED_NAME VARCHAR(120) NOT NULL,
                                    TRIGGER_NAME VARCHAR(190) NOT NULL,
                                    TRIGGER_GROUP VARCHAR(190) NOT NULL,
                                    CRON_EXPRESSION VARCHAR(120) NOT NULL,
                                    TIME_ZONE_ID VARCHAR(80),
                                    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
                                        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_BLOB_TRIGGERS (
                                    SCHED_NAME VARCHAR(120) NOT NULL,
                                    TRIGGER_NAME VARCHAR(190) NOT NULL,
                                    TRIGGER_GROUP VARCHAR(190) NOT NULL,
                                    BLOB_DATA BLOB NULL,
                                    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
                                    INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
                                    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
                                        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_CALENDARS (
                                SCHED_NAME VARCHAR(120) NOT NULL,
                                CALENDAR_NAME VARCHAR(190) NOT NULL,
                                CALENDAR BLOB NOT NULL,
                                PRIMARY KEY (SCHED_NAME,CALENDAR_NAME))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
                                          SCHED_NAME VARCHAR(120) NOT NULL,
                                          TRIGGER_GROUP VARCHAR(190) NOT NULL,
                                          PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_FIRED_TRIGGERS (
                                     SCHED_NAME VARCHAR(120) NOT NULL,
                                     ENTRY_ID VARCHAR(95) NOT NULL,
                                     TRIGGER_NAME VARCHAR(190) NOT NULL,
                                     TRIGGER_GROUP VARCHAR(190) NOT NULL,
                                     INSTANCE_NAME VARCHAR(190) NOT NULL,
                                     FIRED_TIME BIGINT(13) NOT NULL,
                                     SCHED_TIME BIGINT(13) NOT NULL,
                                     PRIORITY INTEGER NOT NULL,
                                     STATE VARCHAR(16) NOT NULL,
                                     JOB_NAME VARCHAR(190) NULL,
                                     JOB_GROUP VARCHAR(190) NULL,
                                     IS_NONCONCURRENT VARCHAR(1) NULL,
                                     REQUESTS_RECOVERY VARCHAR(1) NULL,
                                     PRIMARY KEY (SCHED_NAME,ENTRY_ID))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_SCHEDULER_STATE (
                                      SCHED_NAME VARCHAR(120) NOT NULL,
                                      INSTANCE_NAME VARCHAR(190) NOT NULL,
                                      LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
                                      CHECKIN_INTERVAL BIGINT(13) NOT NULL,
                                      PRIMARY KEY (SCHED_NAME,INSTANCE_NAME))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE QRTZ_LOCKS (
                            SCHED_NAME VARCHAR(120) NOT NULL,
                            LOCK_NAME VARCHAR(40) NOT NULL,
                            PRIMARY KEY (SCHED_NAME,LOCK_NAME))
    ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);

commit;

-- ==================================================================
-- End of the Quartz initialization script
-- ==================================================================