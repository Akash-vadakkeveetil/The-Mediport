-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 11, 2024 at 02:27 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `medisup1`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `username` varchar(255) NOT NULL,
  `med_id` varchar(255) NOT NULL,
  `min_count` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `last_updated` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`username`, `med_id`, `min_count`, `quantity`, `last_updated`) VALUES
('baby123', 'mk42', 200, 600, '2024-05-01'),
('mims123', 'pk100', 200, 135, '2024-05-11'),
('mims123', 'pk101', 100, 200, '2024-05-11');

-- --------------------------------------------------------

--
-- Table structure for table `baby123`
--

CREATE TABLE `baby123` (
  `med_id` varchar(255) NOT NULL,
  `descrip` varchar(255) NOT NULL,
  `price` float NOT NULL,
  `min_count` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `last_updated` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `baby123`
--

INSERT INTO `baby123` (`med_id`, `descrip`, `price`, `min_count`, `quantity`, `last_updated`) VALUES
('mk42', 'coughsyrup-cofsils', 300, 625, 600, '2024-05-01');

-- --------------------------------------------------------

--
-- Table structure for table `login1`
--

CREATE TABLE `login1` (
  `username` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `category` set('pharmacy','supplier','organization') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `login1`
--

INSERT INTO `login1` (`username`, `email`, `password`, `category`) VALUES
('admin', 'admin@gmail.com', 'aaaa', 'organization'),
('baby123', 'baby@gmail.com', 'baby', 'pharmacy'),
('johnson123', 'johnson@gmail.com', 'johnson', 'supplier'),
('mims123', 'mims@gmail.com', 'mims', 'pharmacy'),
('philips123', 'philips@gmail.com', 'philips', 'supplier');

-- --------------------------------------------------------

--
-- Table structure for table `medsup`
--

CREATE TABLE `medsup` (
  `username` varchar(255) NOT NULL,
  `med_id` varchar(255) NOT NULL,
  `descrip` varchar(255) NOT NULL,
  `availability` set('available','not available') NOT NULL DEFAULT 'available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `medsup`
--

INSERT INTO `medsup` (`username`, `med_id`, `descrip`, `availability`) VALUES
('johnson123', 'pk100', 'Zydus Cadilla-Rabbies Vaccine Human', 'available'),
('johnson123', 'pk101', 'Shanchol-Oral Cholera Vaccine', 'available'),
('philips123', 'mk42', 'coughsyrup-cofsils', 'available');

-- --------------------------------------------------------

--
-- Table structure for table `med_order`
--

CREATE TABLE `med_order` (
  `order_id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `med_id` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `sup_id` varchar(255) NOT NULL,
  `ordered_on` date NOT NULL,
  `status` set('not supplied','supplied','received') NOT NULL DEFAULT 'not supplied'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `med_order`
--

INSERT INTO `med_order` (`order_id`, `username`, `med_id`, `quantity`, `sup_id`, `ordered_on`, `status`) VALUES
(1, 'mims123', 'pk100', 100, 'johnson123', '2024-05-01', 'received'),
(2, 'mims123', 'pk101', 1000, 'philips123', '2024-05-02', 'not supplied'),
(3, 'baby123', 'mk42', 500, 'philips123', '2024-05-03', 'not supplied'),
(7, 'mims123', 'pk100', 100, 'johnson123', '2024-05-10', 'received'),
(8, 'mims123', 'pk100', 100, 'johnson123', '2024-05-10', 'received'),
(9, 'mims123', 'pk100', 100, 'johnson123', '2024-05-10', 'received'),
(10, 'mims123', 'pk100', 10, 'johnson123', '2024-05-11', 'supplied'),
(11, 'mims123', 'pk100', 100, 'johnson123', '2024-05-11', 'received');

-- --------------------------------------------------------

--
-- Table structure for table `message`
--

CREATE TABLE `message` (
  `username` varchar(255) NOT NULL,
  `med_id` varchar(255) NOT NULL,
  `msg` varchar(255) NOT NULL,
  `send_on` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `message`
--

INSERT INTO `message` (`username`, `med_id`, `msg`, `send_on`) VALUES
('mims123', 'pk100', 'immediately buy', '2024-05-10'),
('mims123', 'pk100', 'immediately buy', '2024-05-11'),
('mims123', 'pk100', 'Meet the requirement immediately', '2024-05-09');

-- --------------------------------------------------------

--
-- Table structure for table `mims123`
--

CREATE TABLE `mims123` (
  `med_id` varchar(255) NOT NULL,
  `descrip` varchar(255) NOT NULL,
  `price` float NOT NULL,
  `min_count` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `last_updated` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mims123`
--

INSERT INTO `mims123` (`med_id`, `descrip`, `price`, `min_count`, `quantity`, `last_updated`) VALUES
('pk100', 'Zydus Cadilla-Rabbies Vaccine Human', 300, 200, 135, '2024-05-11'),
('pk101', 'Shanchol-Oral Cholera Vaccine', 1000, 100, 200, '2024-05-11');

-- --------------------------------------------------------

--
-- Table structure for table `pharmacy`
--

CREATE TABLE `pharmacy` (
  `username` varchar(255) NOT NULL,
  `pharmacyname` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `pinno` int(11) NOT NULL,
  `contactnumber` bigint(20) NOT NULL,
  `established` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pharmacy`
--

INSERT INTO `pharmacy` (`username`, `pharmacyname`, `location`, `pinno`, `contactnumber`, `established`) VALUES
('baby123', 'baby memorial', 'kannur', 670660, 9988776655, '2022-02-28'),
('mims123', 'mims hospital', 'thalassery', 670670, 9876543210, '2021-02-01');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`username`,`med_id`);

--
-- Indexes for table `baby123`
--
ALTER TABLE `baby123`
  ADD PRIMARY KEY (`med_id`);

--
-- Indexes for table `login1`
--
ALTER TABLE `login1`
  ADD PRIMARY KEY (`username`,`password`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `medsup`
--
ALTER TABLE `medsup`
  ADD PRIMARY KEY (`username`,`med_id`);

--
-- Indexes for table `med_order`
--
ALTER TABLE `med_order`
  ADD PRIMARY KEY (`order_id`);

--
-- Indexes for table `message`
--
ALTER TABLE `message`
  ADD PRIMARY KEY (`username`,`med_id`,`msg`,`send_on`);

--
-- Indexes for table `mims123`
--
ALTER TABLE `mims123`
  ADD PRIMARY KEY (`med_id`),
  ADD UNIQUE KEY `descrip` (`descrip`),
  ADD KEY `descrip_2` (`descrip`),
  ADD KEY `descrip_3` (`descrip`);

--
-- Indexes for table `pharmacy`
--
ALTER TABLE `pharmacy`
  ADD PRIMARY KEY (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `med_order`
--
ALTER TABLE `med_order`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
