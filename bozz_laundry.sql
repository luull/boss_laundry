-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 20, 2026 at 04:11 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bozz_laundry`
--

-- --------------------------------------------------------

--
-- Table structure for table `master_cabang`
--

CREATE TABLE `master_cabang` (
  `id_cabang` int(11) NOT NULL,
  `nama_cabang` varchar(100) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `telepon` varchar(20) DEFAULT NULL,
  `status` enum('aktif','nonaktif') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `master_cabang`
--

INSERT INTO `master_cabang` (`id_cabang`, `nama_cabang`, `alamat`, `telepon`, `status`) VALUES
(1, 'Cibinong', 'Jl raya cibinong	', '081236123', 'aktif'),
(2, 'Cikarang', 'Jl raya cikarang	', '0812371273', 'aktif'),
(4, 'jakarta', 'Tebet ', '081237213', 'nonaktif'),
(6, 'BOGOR', 'jl raya BOGOR', '082361263344', 'aktif');

-- --------------------------------------------------------

--
-- Table structure for table `master_layanan`
--

CREATE TABLE `master_layanan` (
  `id_layanan` int(11) NOT NULL,
  `nama_layanan` varchar(100) DEFAULT NULL,
  `estimasi_hari` int(11) DEFAULT NULL,
  `status` enum('aktif','nonaktif') DEFAULT NULL,
  `kategori` enum('kiloan','addons') DEFAULT NULL,
  `harga_min` decimal(10,2) DEFAULT NULL,
  `harga_max` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `master_layanan`
--

INSERT INTO `master_layanan` (`id_layanan`, `nama_layanan`, `estimasi_hari`, `status`, `kategori`, `harga_min`, `harga_max`) VALUES
(9, 'Cuci Express', 1, 'aktif', 'kiloan', 10000.00, 10000.00),
(10, 'Cuci Reguler', 3, 'aktif', 'kiloan', 7500.00, 7500.00),
(11, 'Seprei', 3, 'aktif', 'addons', 10000.00, 10000.00),
(12, 'Selimut', 3, 'aktif', 'addons', 10000.00, 10000.00),
(13, 'Bed Cover', 3, 'aktif', 'addons', 20000.00, 20000.00),
(14, 'Boneka', 3, 'aktif', 'addons', 10000.00, 50000.00),
(15, 'Tas', 3, 'aktif', 'addons', 20000.00, 50000.00),
(16, 'Karpet', 5, 'aktif', 'addons', 30000.00, 120000.00);

-- --------------------------------------------------------

--
-- Table structure for table `master_pelanggan`
--

CREATE TABLE `master_pelanggan` (
  `id_pelanggan` int(11) NOT NULL,
  `nama_pelanggan` varchar(100) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `no_hp` varchar(20) DEFAULT NULL,
  `jenis_member` enum('regular','vip') DEFAULT NULL,
  `id_cabang` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `master_pelanggan`
--

INSERT INTO `master_pelanggan` (`id_pelanggan`, `nama_pelanggan`, `alamat`, `no_hp`, `jenis_member`, `id_cabang`) VALUES
(1, 'tegar', 'samping mpok imeh	', '0812371237', 'regular', 1),
(2, 'hanif', 'Jakarta	', '0821367123', 'vip', 4),
(3, 'joni', 'sebelah rumah	', '0812381238', 'regular', 2),
(8, 'bima edit', 'jl raya simpang edit	', '0812371273', 'regular', 6),
(9, 'test', NULL, NULL, NULL, 6);

-- --------------------------------------------------------

--
-- Table structure for table `master_user`
--

CREATE TABLE `master_user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('admin','kasir','owner') DEFAULT NULL,
  `id_cabang` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `master_user`
--

INSERT INTO `master_user` (`id_user`, `username`, `password`, `role`, `id_cabang`) VALUES
(1, 'admin', 'admin123', 'admin', NULL),
(2, 'syafa', 'admin123', 'kasir', 1),
(3, 'owner-cibinong', 'admin123', 'owner', 1),
(5, 'admin-bogor', 'admin1234', 'kasir', 6);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi_laundry`
--

CREATE TABLE `transaksi_laundry` (
  `id_transaksi` int(11) NOT NULL,
  `kode_invoice` varchar(100) DEFAULT NULL,
  `id_pelanggan` int(11) DEFAULT NULL,
  `id_layanan` int(11) DEFAULT NULL,
  `berat` double DEFAULT NULL,
  `total_harga` double DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `tanggal_masuk` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `id_cabang` int(11) DEFAULT NULL,
  `addons` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi_laundry`
--

INSERT INTO `transaksi_laundry` (`id_transaksi`, `kode_invoice`, `id_pelanggan`, `id_layanan`, `berat`, `total_harga`, `status`, `tanggal_masuk`, `tanggal_selesai`, `id_cabang`, `addons`) VALUES
(1, 'INV1778485142052', 1, 9, 2, 70000, 'disetrika', '2026-05-11', '2026-05-14', 1, 'Seprei x1, Tas x2, '),
(4, 'INV1778924019273', 1, 9, 5, 70000, 'proses', '2026-05-16', '2026-05-16', 1, 'Seprei, Selimut, '),
(5, 'INV1779249946824', 8, 10, 10, 155000, 'proses', '2026-05-20', '2026-05-23', 1, 'Bed Cover x1, Karpet x2, '),
(6, 'INV1779250059316', 8, 9, 3, 50000, 'dicuci', '2026-05-20', '2026-05-20', 6, 'Seprei, Boneka, '),
(7, 'INV1779285323141', 9, 9, 2, 40000, 'proses', '2026-05-20', '2026-05-20', 6, 'Seprei x2, '),
(8, 'INV1779285599350', 9, 9, 2, 80000, 'proses', '2026-05-20', '2026-05-20', 6, 'Seprei x2, Bed Cover x2, '),
(9, 'INV1779285694402', 8, 9, 3, 60000, 'proses', '2026-05-20', '2026-05-20', 6, 'Seprei x2, Selimut x1, ');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `master_cabang`
--
ALTER TABLE `master_cabang`
  ADD PRIMARY KEY (`id_cabang`);

--
-- Indexes for table `master_layanan`
--
ALTER TABLE `master_layanan`
  ADD PRIMARY KEY (`id_layanan`);

--
-- Indexes for table `master_pelanggan`
--
ALTER TABLE `master_pelanggan`
  ADD PRIMARY KEY (`id_pelanggan`),
  ADD KEY `fk_pelanggan_cabang` (`id_cabang`);

--
-- Indexes for table `master_user`
--
ALTER TABLE `master_user`
  ADD PRIMARY KEY (`id_user`),
  ADD KEY `id_cabang` (`id_cabang`);

--
-- Indexes for table `transaksi_laundry`
--
ALTER TABLE `transaksi_laundry`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `id_pelanggan` (`id_pelanggan`),
  ADD KEY `id_layanan` (`id_layanan`),
  ADD KEY `fk_cabang` (`id_cabang`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `master_cabang`
--
ALTER TABLE `master_cabang`
  MODIFY `id_cabang` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `master_layanan`
--
ALTER TABLE `master_layanan`
  MODIFY `id_layanan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `master_pelanggan`
--
ALTER TABLE `master_pelanggan`
  MODIFY `id_pelanggan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `master_user`
--
ALTER TABLE `master_user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `transaksi_laundry`
--
ALTER TABLE `transaksi_laundry`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `master_pelanggan`
--
ALTER TABLE `master_pelanggan`
  ADD CONSTRAINT `fk_pelanggan_cabang` FOREIGN KEY (`id_cabang`) REFERENCES `master_cabang` (`id_cabang`);

--
-- Constraints for table `master_user`
--
ALTER TABLE `master_user`
  ADD CONSTRAINT `master_user_ibfk_1` FOREIGN KEY (`id_cabang`) REFERENCES `master_cabang` (`id_cabang`);

--
-- Constraints for table `transaksi_laundry`
--
ALTER TABLE `transaksi_laundry`
  ADD CONSTRAINT `fk_cabang` FOREIGN KEY (`id_cabang`) REFERENCES `master_cabang` (`id_cabang`),
  ADD CONSTRAINT `transaksi_laundry_ibfk_1` FOREIGN KEY (`id_pelanggan`) REFERENCES `master_pelanggan` (`id_pelanggan`),
  ADD CONSTRAINT `transaksi_laundry_ibfk_2` FOREIGN KEY (`id_layanan`) REFERENCES `master_layanan` (`id_layanan`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
