-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 02, 2026 at 04:13 AM
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
-- Database: `micro_credential_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `certificates`
--

CREATE TABLE `certificates` (
  `id` int(11) NOT NULL,
  `certificate_uid` varchar(30) NOT NULL,
  `user_uid` varchar(20) NOT NULL,
  `certificate_title` varchar(150) NOT NULL,
  `certificate_type` enum('academic','professional','skill') NOT NULL,
  `issuing_organization` varchar(150) NOT NULL,
  `issue_date` date NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `credential_id` varchar(100) DEFAULT NULL,
  `certificate_file` varchar(255) NOT NULL,
  `verification_status` enum('pending','verified','rejected') DEFAULT 'pending',
  `issuer_verified` tinyint(1) NOT NULL DEFAULT 1,
  `verified_by` varchar(20) DEFAULT NULL,
  `verified_at` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `source` enum('user','issuer') NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `certificates`
--

INSERT INTO `certificates` (`id`, `certificate_uid`, `user_uid`, `certificate_title`, `certificate_type`, `issuing_organization`, `issue_date`, `expiry_date`, `credential_id`, `certificate_file`, `verification_status`, `issuer_verified`, `verified_by`, `verified_at`, `created_at`, `source`) VALUES
(2, 'CERT1766397714407', 'U202523404', 'Oracle AI Foundations', 'professional', 'Oracle', '2024-08-16', '2026-08-16', 'ORCL-12345', 'certificate_CERT1766397714407.pdf', '', 1, 'U202529235', '2025-12-23 08:24:46', '2025-12-22 10:01:54', 'user'),
(3, 'CERT1766458610905', 'U202523404', 'Oracle AI Foundations', 'professional', 'Oracle', '2024-08-16', '2026-08-20', 'ORCL-12345', 'certificate_CERT1766458610905.pdf', 'verified', 1, 'U202529235', '2025-12-26 12:33:48', '2025-12-23 02:56:50', 'user'),
(4, 'CERT1766464177377', 'U202523404', 'Oracle AI Foundations', 'professional', 'Oracle', '2024-08-16', '2026-08-20', 'ORCL-12345', 'certificate_CERT1766464177377.pdf', 'pending', 1, NULL, NULL, '2025-12-23 04:29:37', 'user'),
(5, 'CERT1766465136595', 'U202523404', 'Oracle AI Foundations', 'professional', 'Oracle', '2024-08-16', '2026-08-20', 'ORCL-12345', 'certificate_CERT1766465136595.pdf', 'pending', 1, NULL, NULL, '2025-12-23 04:45:36', 'user'),
(6, 'CERT1766465198141', 'U202523404', 'Oracle AI Foundations', 'professional', 'Oracle', '2024-08-16', '2026-08-20', 'ORCL-12345', 'certificate_CERT1766465198141.pdf', 'pending', 1, NULL, NULL, '2025-12-23 04:46:38', 'user'),
(7, 'CERT1766465419390', 'U202523404', 'Oracle AI Foundations', 'professional', 'Oracle', '2024-08-16', '2026-08-20', 'ORCL-12345', 'certificate_CERT1766465419390.pdf', 'pending', 1, NULL, NULL, '2025-12-23 04:50:19', 'user'),
(8, 'CERT1766732298514', 'U202548341', 'Java Foundations', 'professional', 'Oracle', '2024-08-16', '2026-08-20', 'ORA-JAVA-001', 'certificate_CERT1766732298514.pdf', 'verified', 1, 'U202529235', '2025-12-26 12:37:23', '2025-12-26 06:58:18', 'user'),
(9, 'CERT1766734974820', 'U202523404', 'Java Foundations', 'professional', 'Oracle', '2024-06-01', '2026-06-01', 'ORA001', 'certificate_CERT1766734974820.pdf', 'verified', 1, 'U202564861', '2025-12-26 13:12:54', '2025-12-26 07:42:54', 'issuer'),
(10, 'CERT1766734974508', 'U202540085', 'Java Foundations', 'professional', 'Oracle', '2024-06-01', '2026-06-01', 'ORA002', 'certificate_CERT1766734974508.pdf', 'verified', 1, 'U202564861', '2025-12-26 13:12:54', '2025-12-26 07:42:54', 'issuer'),
(11, 'CERT1766812561676', 'U202529235', 'Java Foundations', 'professional', 'Oracle University', '2024-08-16', '2026-08-20', 'ORA-JAVA-001', 'certificate_CERT1766812561676.pdf', 'pending', 1, NULL, NULL, '2025-12-27 05:16:01', 'user'),
(12, 'CERT1766812642663', 'U202529235', 'Java Foundations', 'professional', 'XYZ Skill Academy', '2024-08-16', '2026-08-20', 'ORA-JAVA-001', 'certificate_CERT1766812642663.pdf', 'pending', 0, NULL, NULL, '2025-12-27 05:17:22', 'user'),
(13, 'CERT1766993364754', 'U202523404', 'Web Development', 'academic', 'Oracle University', '2025-12-01', '2025-12-31', 'ORA', 'certificate_CERT1766993364754.png', 'pending', 1, NULL, NULL, '2025-12-29 07:29:24', 'user'),
(14, 'CERT1766993397249', 'U202523404', 'Web Development', 'academic', 'Oracle University', '2025-12-01', '2025-12-31', 'ORA', 'certificate_CERT1766993397249.png', 'pending', 1, NULL, NULL, '2025-12-29 07:29:57', 'user'),
(15, 'CERT1766993980369', 'U202523404', 'Web Development', 'academic', 'Oracle University', '2025-12-01', '2025-12-31', 'ORA', 'certificate_CERT1766993980369.jpg', 'pending', 1, NULL, NULL, '2025-12-29 07:39:40', 'user'),
(16, 'CERT1766994168279', 'U202523404', 'resume', 'academic', 'Oracle University', '2025-12-01', '2025-12-31', 'ORA', 'certificate_CERT1766994168279.pdf', 'pending', 1, NULL, NULL, '2025-12-29 07:42:48', 'user'),
(17, 'CERT1766994911850', 'U202523404', 'check 1', 'academic', 'Oracle University', '2025-12-02', '2025-12-31', 'ORA', 'certificate_CERT1766994911850.jpg', 'pending', 1, NULL, NULL, '2025-12-29 07:55:11', 'user');

-- --------------------------------------------------------

--
-- Table structure for table `certificate_skill_mapping`
--

CREATE TABLE `certificate_skill_mapping` (
  `id` int(11) NOT NULL,
  `issuer` varchar(100) NOT NULL,
  `certificate_title` varchar(150) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `nsqf_level` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `certificate_skill_mapping`
--

INSERT INTO `certificate_skill_mapping` (`id`, `issuer`, `certificate_title`, `skill_id`, `nsqf_level`) VALUES
(5, 'Oracle', 'Oracle AI Foundations', 2, 3),
(6, 'Oracle', 'Oracle AI Foundations', 3, 3),
(7, 'AWS', 'Cloud Practitioner', 1, 5);

-- --------------------------------------------------------

--
-- Table structure for table `certificate_verification`
--

CREATE TABLE `certificate_verification` (
  `id` int(11) NOT NULL,
  `certificate_uid` varchar(30) NOT NULL,
  `status` enum('pending','verified','rejected') DEFAULT 'pending',
  `verified_by` varchar(20) DEFAULT NULL,
  `verified_at` datetime DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `certificate_verification`
--

INSERT INTO `certificate_verification` (`id`, `certificate_uid`, `status`, `verified_by`, `verified_at`, `remarks`, `created_at`) VALUES
(3, 'CERT1766397714407', '', 'U202529235', '2025-12-23 08:24:46', 'Certificate verified successfully', '2025-12-23 02:54:46'),
(4, 'CERT1766458610905', 'verified', 'U202529235', '2025-12-23 08:28:25', 'Certificate verified successfully', '2025-12-23 02:58:25');

-- --------------------------------------------------------

--
-- Table structure for table `issuers`
--

CREATE TABLE `issuers` (
  `issuer_id` int(11) NOT NULL,
  `user_uid` varchar(20) DEFAULT NULL,
  `issuer_name` varchar(150) DEFAULT NULL,
  `issuer_type` varchar(50) DEFAULT NULL,
  `verified` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `issuers`
--

INSERT INTO `issuers` (`issuer_id`, `user_uid`, `issuer_name`, `issuer_type`, `verified`, `created_at`) VALUES
(1, 'U202564861', 'Oracle University', NULL, 1, '2025-12-26 06:42:52');

-- --------------------------------------------------------

--
-- Table structure for table `login_logs`
--

CREATE TABLE `login_logs` (
  `id` int(11) NOT NULL,
  `user_uid` varchar(20) NOT NULL,
  `login_time` datetime NOT NULL,
  `logout_time` datetime DEFAULT NULL,
  `ip_address` varchar(50) DEFAULT NULL,
  `device_info` varchar(255) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `session_token` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `login_logs`
--

INSERT INTO `login_logs` (`id`, `user_uid`, `login_time`, `logout_time`, `ip_address`, `device_info`, `country`, `city`, `session_token`, `created_at`) VALUES
(1, 'U202523404', '2025-12-19 17:30:19', '2025-12-19 17:53:56', '::1', 'PostmanRuntime/7.49.1', 'Unknown', 'Unknown', 'qfud7rcbnsfer1137bnq7tjbq8', '2025-12-19 12:00:19'),
(2, 'U202523404', '2025-12-19 17:47:56', '2025-12-19 17:53:56', '::1', 'PostmanRuntime/7.49.1', 'Unknown', 'Unknown', 'qfud7rcbnsfer1137bnq7tjbq8', '2025-12-19 12:17:56'),
(3, 'U202523404', '2025-12-19 17:54:09', NULL, '::1', 'PostmanRuntime/7.49.1', 'Unknown', 'Unknown', 'qfud7rcbnsfer1137bnq7tjbq8', '2025-12-19 12:24:09'),
(4, 'U202523404', '2025-12-22 15:24:45', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'r9oa03tnuapgs8j35pu5vntrrp', '2025-12-22 09:54:45'),
(5, 'U202529235', '2025-12-22 15:35:53', '2025-12-22 15:43:17', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'r9oa03tnuapgs8j35pu5vntrrp', '2025-12-22 10:05:53'),
(6, 'U202529235', '2025-12-22 15:40:28', '2025-12-22 15:43:17', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'r9oa03tnuapgs8j35pu5vntrrp', '2025-12-22 10:10:28'),
(7, 'U202542053', '2025-12-22 15:53:34', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'r9oa03tnuapgs8j35pu5vntrrp', '2025-12-22 10:23:34'),
(8, 'U202542053', '2025-12-23 08:06:11', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'hrdncduuu5n17c72acacv6a83b', '2025-12-23 02:36:11'),
(9, 'U202542053', '2025-12-23 08:09:58', '2025-12-23 08:23:18', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 02:39:58'),
(10, 'U202529235', '2025-12-23 08:23:53', '2025-12-23 10:34:13', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 02:53:53'),
(11, 'U202523404', '2025-12-23 08:26:20', '2025-12-23 08:27:02', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 02:56:20'),
(12, 'U202529235', '2025-12-23 08:27:40', '2025-12-23 10:34:13', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 02:57:40'),
(13, 'U202542053', '2025-12-23 08:29:12', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 02:59:12'),
(14, 'U202523404', '2025-12-23 09:59:31', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 04:29:31'),
(15, 'U202529235', '2025-12-23 10:32:17', '2025-12-23 10:34:13', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 05:02:17'),
(16, 'U202529235', '2025-12-23 10:34:00', '2025-12-23 10:34:13', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 05:04:00'),
(17, 'U202523404', '2025-12-23 10:34:32', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'f49ce4sbg7tg1al6rl2d6d3pvo', '2025-12-23 05:04:32'),
(18, 'U202523404', '2025-12-24 13:11:49', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'ciiuuh1gstt9nu0tr9cebu50e9', '2025-12-24 07:41:49'),
(19, 'U202523404', '2025-12-24 13:11:50', NULL, '::1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'vcetq3ji5to840mmhifuou2qkf', '2025-12-24 07:41:50'),
(20, 'U202523404', '2025-12-24 13:11:50', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'iccch4lskd21qej40b5ajvhl7j', '2025-12-24 07:41:50'),
(21, 'U202523404', '2025-12-24 13:11:51', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'adl7k821sodimidr8rjnd2jk1s', '2025-12-24 07:41:51'),
(22, 'U202523404', '2025-12-24 13:21:26', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'm7un0007skre6689rakjtigesf', '2025-12-24 07:51:26'),
(23, 'U202529235', '2025-12-24 13:21:52', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'a64e6rkk9qd7pskf2k5ttqrvtl', '2025-12-24 07:51:52'),
(24, 'U202523404', '2025-12-24 13:22:21', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'ge5cbu4t8sl1n86pvghusjodpe', '2025-12-24 07:52:21'),
(25, 'U202542053', '2025-12-24 13:40:37', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', '88lf902r7j164kmmctbuinb0nm', '2025-12-24 08:10:37'),
(26, 'U202542053', '2025-12-24 13:46:35', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'fpp8ngokpl6mkko9u1cnapc4kh', '2025-12-24 08:16:35'),
(27, 'U202542053', '2025-12-24 13:47:44', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '3uicivmu46fvnkl5s6k8avm0sm', '2025-12-24 08:17:44'),
(28, 'U202529235', '2025-12-24 13:48:34', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'p3s8b3fe9do42qeo2a9j1ss0c4', '2025-12-24 08:18:34'),
(29, 'U202523404', '2025-12-24 13:49:28', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'j5t4eanretpbpgq9ansq3osohk', '2025-12-24 08:19:28'),
(30, 'U202523404', '2025-12-24 13:51:57', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 16; SM-A736B Build/BP2A.250605.031.A3)', 'Unknown', 'Unknown', 'cq9mh4698kh5fqu0pt2e2ropj5', '2025-12-24 08:21:57'),
(31, 'U202529235', '2025-12-24 13:53:03', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 16; SM-A736B Build/BP2A.250605.031.A3)', 'Unknown', 'Unknown', '13rb1nilnolk49dourm5n46pmk', '2025-12-24 08:23:03'),
(32, 'U202523404', '2025-12-24 13:54:41', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 13; SM-M526B Build/TP1A.220624.014)', 'Unknown', 'Unknown', '7qo2enihb6s2rbe8u89g6qjdpv', '2025-12-24 08:24:41'),
(33, 'U202523404', '2025-12-24 19:19:43', '2025-12-25 22:20:55', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'nst90lho1s1s853tltfa01akcg', '2025-12-24 13:49:43'),
(34, 'U202523404', '2025-12-24 19:37:06', NULL, '::1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'vn8k5bvij5fulbkgq69uj1v5ti', '2025-12-24 14:07:06'),
(35, 'U202523404', '2025-12-25 13:36:40', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 't033d924skqudpug33ra3v6k2g', '2025-12-25 08:06:40'),
(36, 'U202523404', '2025-12-25 13:36:40', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'l01ia9cmapf66kd6rnoj88vd4i', '2025-12-25 08:06:40'),
(37, 'U202523404', '2025-12-25 13:36:45', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '4apq2ecv42kea25n25988of2dg', '2025-12-25 08:06:45'),
(38, 'U202523404', '2025-12-25 13:36:45', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '13ir9m8qatrhbqdcm5an084bhc', '2025-12-25 08:06:45'),
(39, 'U202523404', '2025-12-25 13:36:46', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'lvdgk7q4a6pi5s20b2aur80d9j', '2025-12-25 08:06:46'),
(40, 'U202523404', '2025-12-25 13:36:46', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'e1r4at77o2n1kau4c3aotc0gg2', '2025-12-25 08:06:46'),
(41, 'U202523404', '2025-12-25 13:36:47', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '1r2blmf4t1rvkiq133uhba561b', '2025-12-25 08:06:47'),
(42, 'U202523404', '2025-12-25 13:36:47', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '8acouv7v8onnere9l9gs48c449', '2025-12-25 08:06:47'),
(43, 'U202540085', '2025-12-25 13:38:03', NULL, '::1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '9srhpqab9247or14mivmoimvi6', '2025-12-25 08:08:03'),
(44, 'U202523404', '2025-12-25 13:45:54', '2025-12-25 22:20:55', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'nst90lho1s1s853tltfa01akcg', '2025-12-25 08:15:54'),
(45, 'U202523404', '2025-12-25 13:46:21', '2025-12-25 22:20:55', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'nst90lho1s1s853tltfa01akcg', '2025-12-25 08:16:21'),
(46, 'U202540085', '2025-12-25 13:47:26', NULL, '::1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'pterntu2n91itmgnmuk6k9775h', '2025-12-25 08:17:26'),
(47, 'U202540085', '2025-12-25 14:06:39', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'ddqnh3sirh2e9o9hhsn21mlu5o', '2025-12-25 08:36:39'),
(48, 'U202540085', '2025-12-25 17:21:51', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'aoi3upr584nar5dh36lvrfehjh', '2025-12-25 11:51:51'),
(49, 'U202540085', '2025-12-25 17:22:41', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'rimp68s7cfpnkps7hsv5m4m1ac', '2025-12-25 11:52:41'),
(50, 'U202540085', '2025-12-25 17:25:16', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '30tlmgaagqqkmgkifihejfgv4j', '2025-12-25 11:55:16'),
(51, 'U202540085', '2025-12-25 17:31:07', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'bt42guia43e61fmhlqgchk6svf', '2025-12-25 12:01:07'),
(52, 'U202523404', '2025-12-25 18:17:52', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'gjp625ueuvug6jvgu88jkgs0o0', '2025-12-25 12:47:52'),
(53, 'U202529235', '2025-12-25 18:18:22', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'nudk0767627cj9gub02nlsdk64', '2025-12-25 12:48:22'),
(54, 'U202542053', '2025-12-25 18:19:06', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'srp8mua8quottnnkf9d5qpv43d', '2025-12-25 12:49:06'),
(55, 'U202542053', '2025-12-25 18:19:07', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'morbm9gb494jnpgb6i6e1iq07u', '2025-12-25 12:49:07'),
(56, 'U202542053', '2025-12-25 18:19:08', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'lkcu5qaevih3vosc82mtogem4g', '2025-12-25 12:49:08'),
(57, 'U202523404', '2025-12-25 22:17:15', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'bb9a69th9efo85mt8v3u50o76j', '2025-12-25 16:47:15'),
(58, 'U202523404', '2025-12-25 22:19:30', '2025-12-25 22:20:55', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'nst90lho1s1s853tltfa01akcg', '2025-12-25 16:49:30'),
(59, 'U202529235', '2025-12-25 22:21:09', '2025-12-25 22:21:22', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'nst90lho1s1s853tltfa01akcg', '2025-12-25 16:51:09'),
(60, 'U202523404', '2025-12-25 22:35:24', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'vqrr82mlr1sh9gigq114t4mle5', '2025-12-25 17:05:24'),
(61, 'U202523404', '2025-12-25 22:36:10', NULL, '::1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'apdqtj0ova9ko0u43c2bp5ro5v', '2025-12-25 17:06:10'),
(62, 'U202523404', '2025-12-25 22:36:14', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '6hqfl1m7shvd5btghdk00jdlr7', '2025-12-25 17:06:14'),
(63, 'U202523404', '2025-12-25 22:36:36', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'p0dkrgqo9da21m13v3q05gjeul', '2025-12-25 17:06:36'),
(64, 'U202523404', '2025-12-25 22:52:45', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '279hcsk6jhvnfi0uvtu6smq3fq', '2025-12-25 17:22:45'),
(65, 'U202529235', '2025-12-25 22:53:27', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'enedqhj214f9m78hk7lt5nsfi8', '2025-12-25 17:23:27'),
(66, 'U202542053', '2025-12-25 22:54:00', NULL, '::1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '7297cmetubpq9ecde7ro03o5uv', '2025-12-25 17:24:00'),
(67, 'U202529235', '2025-12-25 22:55:47', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'nst90lho1s1s853tltfa01akcg', '2025-12-25 17:25:47'),
(68, 'U202529235', '2025-12-25 22:59:02', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '2ikmmtcfcr2rub3grrdctsh3bp', '2025-12-25 17:29:02'),
(69, 'U202523404', '2025-12-25 22:59:39', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'au2ipmrjnko0r2jh67csbq7u8b', '2025-12-25 17:29:39'),
(70, 'U202529235', '2025-12-25 23:12:01', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'v2fue8j2t1jnnj2ccub5s580ks', '2025-12-25 17:42:01'),
(71, 'U202542053', '2025-12-25 23:12:32', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'pihldk3gb60ogr0rgll8vksu5k', '2025-12-25 17:42:32'),
(72, 'U202523404', '2025-12-25 23:13:06', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'vilce3bfm8vf3guvsnpsr9d6k6', '2025-12-25 17:43:06'),
(73, 'U202523404', '2025-12-25 23:14:26', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'pul6u63bde2pd2275jkbu4m31e', '2025-12-25 17:44:26'),
(74, 'U202540085', '2025-12-25 23:14:58', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '2jtlsb11sfjce9u87pbv6q4c5c', '2025-12-25 17:44:58'),
(75, 'U202523404', '2025-12-25 23:16:22', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'o6ctpu8pirpjvh0llka6uis353', '2025-12-25 17:46:22'),
(76, 'U202523404', '2025-12-25 23:18:20', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'vpvipsn9rrttsvrvhh66cdpomh', '2025-12-25 17:48:20'),
(77, 'U202529235', '2025-12-25 23:19:34', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'so25c4rghl6nvhh4sgdnb9l7i1', '2025-12-25 17:49:34'),
(78, 'U202542053', '2025-12-25 23:19:58', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'st8h71m97o2nn2l1vmg4o8dr2f', '2025-12-25 17:49:58'),
(79, 'U202523404', '2025-12-25 23:35:10', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '1ur8bpupj12ni39lqje57g02vs', '2025-12-25 18:05:10'),
(80, 'U202542053', '2025-12-25 23:36:14', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'mjqvu8jn4k6k5bv6vei2obrc13', '2025-12-25 18:06:14'),
(81, 'U202529235', '2025-12-25 23:36:53', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 's326tm2ivclpluef0q76ire8j0', '2025-12-25 18:06:53'),
(82, 'U202542053', '2025-12-25 23:37:16', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'nst90lho1s1s853tltfa01akcg', '2025-12-25 18:07:16'),
(83, 'U202542053', '2025-12-25 23:37:45', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'ecqdp6j282bt5qu369g7nt24r7', '2025-12-25 18:07:45'),
(84, 'U202529235', '2025-12-25 23:38:22', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'c3hb3uja7fi502civnch38o99m', '2025-12-25 18:08:22'),
(85, 'U202523404', '2025-12-25 23:42:27', '2025-12-25 23:42:58', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '6cu18j72uci9sr3g8irbcarqfj', '2025-12-25 18:12:27'),
(86, 'U202542053', '2025-12-25 23:43:56', '2025-12-25 23:44:04', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'q0ncaoi73jh0e2140h5biu2fp4', '2025-12-25 18:13:56'),
(87, 'U202542053', '2025-12-25 23:48:24', '2025-12-25 23:48:29', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'vbp5do9h2aamruhd1so2gjbf4v', '2025-12-25 18:18:24'),
(88, 'U202523404', '2025-12-26 08:13:34', '2025-12-26 08:16:00', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'jgn47ldjpjh2jk9qnvdfjn8nhl', '2025-12-26 02:43:34'),
(89, 'U202523404', '2025-12-26 08:14:58', '2025-12-26 13:35:40', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'etv504k52sjcvit0k2sguplv3e', '2025-12-26 02:44:58'),
(90, 'U202523404', '2025-12-26 08:19:31', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'lonlv343h99risoksg8l7eg9uk', '2025-12-26 02:49:31'),
(91, 'U202548341', '2025-12-26 12:13:22', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'lonlv343h99risoksg8l7eg9uk', '2025-12-26 06:43:22'),
(92, 'U202529235', '2025-12-26 12:33:40', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'lonlv343h99risoksg8l7eg9uk', '2025-12-26 07:03:40'),
(93, 'U202523404', '2025-12-26 12:38:40', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'c34mhdghkvuir3j8f0lsn1rmct', '2025-12-26 07:08:40'),
(94, 'U202564861', '2025-12-26 12:55:26', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'c34mhdghkvuir3j8f0lsn1rmct', '2025-12-26 07:25:26'),
(95, 'U202564861', '2025-12-26 13:25:58', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'c34mhdghkvuir3j8f0lsn1rmct', '2025-12-26 07:55:58'),
(96, 'U202564861', '2025-12-26 13:33:25', '2025-12-26 13:33:42', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'ev8aa9v0arvkm5g414dlgoidgg', '2025-12-26 08:03:25'),
(97, 'U202564861', '2025-12-26 13:36:14', '2025-12-26 13:40:20', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'rpa4a8m610126p47mlt534dob0', '2025-12-26 08:06:14'),
(98, 'U202564861', '2025-12-26 13:37:22', '2025-12-26 13:37:52', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '6kb6c6g2l7v6k0e14u65o7is8s', '2025-12-26 08:07:22'),
(99, 'U202564861', '2025-12-26 13:40:56', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '5nubjrcp5et4mjnsqhdtis51io', '2025-12-26 08:10:56'),
(100, 'U202564861', '2025-12-26 13:40:56', '2025-12-26 13:41:47', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'nc74jokldvddfphphr9upmrkk3', '2025-12-26 08:10:56'),
(101, 'U202523404', '2025-12-26 14:00:31', '2025-12-26 14:35:04', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '8230e39u87ud7luei6079hgttd', '2025-12-26 08:30:31'),
(102, 'U202523404', '2025-12-26 14:04:44', '2025-12-26 14:49:51', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '1mc516sngijc8ggg4o6mlotfat', '2025-12-26 08:34:44'),
(103, 'U202523404', '2025-12-26 14:35:44', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', '0m316nrv4vd7gqpbqq5fgcqnio', '2025-12-26 09:05:44'),
(104, 'U202523404', '2025-12-26 14:50:04', '2025-12-26 14:50:29', '::1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'hnc7ug8jmrlep9e0e3i7rf2d68', '2025-12-26 09:20:04'),
(105, 'U202523404', '2025-12-27 08:59:24', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'ad4c0s36uo04mbse7p60dgm3ns', '2025-12-27 03:29:24'),
(106, 'U202523404', '2025-12-27 10:06:27', '2025-12-27 10:30:33', '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'fvkst3q7pm52ni2o0sj5q25ibl', '2025-12-27 04:36:27'),
(107, 'U202529235', '2025-12-27 10:30:45', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', 'fvkst3q7pm52ni2o0sj5q25ibl', '2025-12-27 05:00:45'),
(108, 'U202523404', '2025-12-27 12:38:31', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'qlap29uq0ge41pt8ujnh4ia2uo', '2025-12-27 07:08:31'),
(109, 'U202523404', '2025-12-27 13:02:44', '2025-12-27 13:47:41', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'atb8vskla9f1m132n2cmrfjcv9', '2025-12-27 07:32:44'),
(110, 'U202523404', '2025-12-27 13:48:10', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'i1q4musf9kl0f29gknime4o042', '2025-12-27 08:18:10'),
(111, 'U202523404', '2025-12-29 12:32:47', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'c59jgbm1l8oo2fcaie36ekna6c', '2025-12-29 07:02:47'),
(112, 'U202523404', '2025-12-29 12:52:06', '2025-12-29 13:09:40', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'o36kf4mf63097mq920h55th8c0', '2025-12-29 07:22:06'),
(113, 'U202523404', '2025-12-29 12:54:30', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'ujmh1vokq04phj2kie3i66iplg', '2025-12-29 07:24:30'),
(114, 'U202523404', '2025-12-29 13:11:37', '2025-12-29 15:29:29', '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', 'g4ilad6t0aolauuddo7pv8j7l5', '2025-12-29 07:41:37'),
(115, 'U202523404', '2025-12-29 13:32:47', NULL, '::1', 'PostmanRuntime/7.51.0', 'Unknown', 'Unknown', '3embikr4fldt8qr48e5b3mbrri', '2025-12-29 08:02:47'),
(116, 'U202523404', '2025-12-29 15:29:44', NULL, '::1', 'Dalvik/2.1.0 (Linux; U; Android 12; SM-A217F Build/SP1A.210812.016)', 'Unknown', 'Unknown', '6kbj0arucgs5rk2hq03vcd9tmp', '2025-12-29 09:59:44'),
(117, 'U202523404', '2025-12-29 15:34:33', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'vcsjopv642m4s3p1ll951rc0fi', '2025-12-29 10:04:33'),
(118, 'U202523404', '2026-01-02 08:27:09', NULL, '127.0.0.1', 'Dalvik/2.1.0 (Linux; U; Android 14; sdk_gphone64_x86_64 Build/UE1A.230829.050)', 'Unknown', 'Unknown', 'gp27pv3hodeh2vilcp23oro5eh', '2026-01-02 02:57:09');

-- --------------------------------------------------------

--
-- Table structure for table `pending_certificates`
--

CREATE TABLE `pending_certificates` (
  `id` int(11) NOT NULL,
  `email` varchar(150) NOT NULL,
  `certificate_uid` varchar(30) DEFAULT NULL,
  `certificate_title` varchar(150) NOT NULL,
  `certificate_type` enum('academic','professional','skill') NOT NULL,
  `issuing_organization` varchar(150) NOT NULL,
  `issue_date` date NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `credential_id` varchar(100) DEFAULT NULL,
  `certificate_file` varchar(255) NOT NULL,
  `issued_by` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pending_certificates`
--

INSERT INTO `pending_certificates` (`id`, `email`, `certificate_uid`, `certificate_title`, `certificate_type`, `issuing_organization`, `issue_date`, `expiry_date`, `credential_id`, `certificate_file`, `issued_by`, `created_at`) VALUES
(1, 'student1@test.com', 'CERT1766734656604', 'Java Foundations', 'professional', 'Oracle', '2024-06-01', '2026-06-01', 'ORA001', 'certificate_CERT1766734656604.pdf', 'U202564861', '2025-12-26 07:37:36'),
(2, 'student2@test.com', 'CERT1766734656665', 'Java Foundations', 'professional', 'Oracle', '2024-06-01', '2026-06-01', 'ORA002', 'certificate_CERT1766734656665.pdf', 'U202564861', '2025-12-26 07:37:36');

-- --------------------------------------------------------

--
-- Table structure for table `skills_master`
--

CREATE TABLE `skills_master` (
  `skill_id` int(11) NOT NULL,
  `skill_name` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `skills_master`
--

INSERT INTO `skills_master` (`skill_id`, `skill_name`, `category`) VALUES
(1, 'Cloud Computing', 'Technology'),
(2, 'Artificial Intelligence', 'Technology'),
(3, 'Machine Learning', 'Technology'),
(4, 'Java Programming', 'Technology');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `user_uid` varchar(20) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','user','employer','issuer') NOT NULL DEFAULT 'user',
  `status` enum('active','inactive') NOT NULL DEFAULT 'active',
  `dob` date DEFAULT NULL,
  `gender` enum('male','female','other') DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `user_uid`, `full_name`, `email`, `password`, `role`, `status`, `dob`, `gender`, `created_at`) VALUES
(1, 'U202523404', 'Charan Gundlapalli', 'charan@test.com', '$2y$10$RN1Nk4eD1rD5Et09smomhuJlrRhvlL66oPVeiR6VtJk27HtEKFc86', 'user', 'active', NULL, NULL, '2025-12-19 11:51:13'),
(2, 'U202529235', 'Charan', 'charan@test1.com', '$2y$10$PALs6GJmfU8TSPE8lyqpAeqzTTtEOw3625Od3MXpeK2qhTJNb7af6', 'admin', 'active', NULL, NULL, '2025-12-22 10:05:32'),
(3, 'U202542053', 'Gundlapalli', 'charan@test2.com', '$2y$10$ffxO5Lc/u5rbbGQV6hkoqu/nT8sz5MTo3tyiz6OYYKLpVv625M3vi', 'employer', 'active', NULL, NULL, '2025-12-22 10:23:14'),
(4, 'U202540085', 'saveetha', 'saveetha@gmail.com', '$2y$10$lQseeFfVSk.tQ2soidb6Gu7d/MHTVLn25hYuZ8G7tQ9gjb5P2s.KK', 'user', 'active', NULL, NULL, '2025-12-25 08:07:12'),
(5, 'U202548341', 'Charan User', 'charan.user@test.com', '$2y$10$tUC7j7PoSRGtNG3hGkjz2.sq2NoezSS1NR6xjzpL8vbqVpcVdu.Tu', 'user', 'active', NULL, NULL, '2025-12-26 06:42:25'),
(6, 'U202564861', 'Oracle University', 'oracle@test.com', '$2y$10$DA7Z5VIc6JSOPLlPpC34xua8bSSfjOmeqD1INmRfG1cuircMU2.bq', 'issuer', 'active', NULL, NULL, '2025-12-26 06:42:52');

-- --------------------------------------------------------

--
-- Table structure for table `user_skills`
--

CREATE TABLE `user_skills` (
  `id` int(11) NOT NULL,
  `user_uid` varchar(20) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `nsqf_level` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_skills`
--

INSERT INTO `user_skills` (`id`, `user_uid`, `skill_id`, `nsqf_level`) VALUES
(1, 'U202523404', 2, 3),
(2, 'U202523404', 3, 3);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `certificates`
--
ALTER TABLE `certificates`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `certificate_uid` (`certificate_uid`),
  ADD KEY `user_uid` (`user_uid`);

--
-- Indexes for table `certificate_skill_mapping`
--
ALTER TABLE `certificate_skill_mapping`
  ADD PRIMARY KEY (`id`),
  ADD KEY `skill_id` (`skill_id`);

--
-- Indexes for table `certificate_verification`
--
ALTER TABLE `certificate_verification`
  ADD PRIMARY KEY (`id`),
  ADD KEY `certificate_uid` (`certificate_uid`);

--
-- Indexes for table `issuers`
--
ALTER TABLE `issuers`
  ADD PRIMARY KEY (`issuer_id`),
  ADD UNIQUE KEY `user_uid` (`user_uid`);

--
-- Indexes for table `login_logs`
--
ALTER TABLE `login_logs`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pending_certificates`
--
ALTER TABLE `pending_certificates`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `certificate_uid` (`certificate_uid`),
  ADD KEY `email` (`email`);

--
-- Indexes for table `skills_master`
--
ALTER TABLE `skills_master`
  ADD PRIMARY KEY (`skill_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_uid` (`user_uid`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_skills`
--
ALTER TABLE `user_skills`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_uid` (`user_uid`,`skill_id`),
  ADD KEY `skill_id` (`skill_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `certificates`
--
ALTER TABLE `certificates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `certificate_skill_mapping`
--
ALTER TABLE `certificate_skill_mapping`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `certificate_verification`
--
ALTER TABLE `certificate_verification`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `issuers`
--
ALTER TABLE `issuers`
  MODIFY `issuer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `login_logs`
--
ALTER TABLE `login_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=119;

--
-- AUTO_INCREMENT for table `pending_certificates`
--
ALTER TABLE `pending_certificates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `skills_master`
--
ALTER TABLE `skills_master`
  MODIFY `skill_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `user_skills`
--
ALTER TABLE `user_skills`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `certificates`
--
ALTER TABLE `certificates`
  ADD CONSTRAINT `certificates_ibfk_1` FOREIGN KEY (`user_uid`) REFERENCES `users` (`user_uid`) ON DELETE CASCADE;

--
-- Constraints for table `certificate_skill_mapping`
--
ALTER TABLE `certificate_skill_mapping`
  ADD CONSTRAINT `certificate_skill_mapping_ibfk_1` FOREIGN KEY (`skill_id`) REFERENCES `skills_master` (`skill_id`);

--
-- Constraints for table `certificate_verification`
--
ALTER TABLE `certificate_verification`
  ADD CONSTRAINT `certificate_verification_ibfk_1` FOREIGN KEY (`certificate_uid`) REFERENCES `certificates` (`certificate_uid`) ON DELETE CASCADE;

--
-- Constraints for table `user_skills`
--
ALTER TABLE `user_skills`
  ADD CONSTRAINT `user_skills_ibfk_1` FOREIGN KEY (`skill_id`) REFERENCES `skills_master` (`skill_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
