-- MySQL dump 10.13  Distrib 5.6.26, for Linux (x86_64)
--
-- Host: localhost    Database: promisdb
-- ------------------------------------------------------
-- Server version	5.6.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `averages`
--

DROP TABLE IF EXISTS `averages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `averages` (
  `avg_id` int(11) NOT NULL AUTO_INCREMENT,
  `avg_ratio1` float(10,2) DEFAULT NULL,
  `avg_ratio2` float(10,2) DEFAULT NULL,
  `sum_total_count` int(11) DEFAULT NULL,
  `avg_time` datetime DEFAULT NULL,
  `clients` int(11) DEFAULT NULL,
  PRIMARY KEY (`avg_id`),
  KEY `entry_time` (`avg_time`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_version`
--

DROP TABLE IF EXISTS `client_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_version` (
  `ver_id` int(11) NOT NULL AUTO_INCREMENT,
  `ver_client_id` varchar(50) NOT NULL,
  `ver_entry_time` datetime NOT NULL,
  `ver_version` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ver_id`),
  KEY `client_id` (`ver_client_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `error`
--

DROP TABLE IF EXISTS `error`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `error` (
  `er_id` int(11) NOT NULL AUTO_INCREMENT,
  `er_client_id` varchar(50) NOT NULL,
  `er_entry_time` datetime NOT NULL,
  `er_error_msg` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`er_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `localIPs`
--

DROP TABLE IF EXISTS `localIPs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `localIPs` (
  `lclip_id` int(11) NOT NULL AUTO_INCREMENT,
  `lclip_ip` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`lclip_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `packets`
--

DROP TABLE IF EXISTS `packets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `packets` (
  `pc_id` int(11) NOT NULL AUTO_INCREMENT,
  `pc_client_id` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  `action` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `protocol` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `srcip` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dstip` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `srcport` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dstport` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `size` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tcpflags` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tcpsyn` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tcpack` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tcpwin` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `icmptype` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `icmpcode` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `info` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `path` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `public_ip` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`pc_id`),
  KEY `entry_time` (`datetime`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ratio`
--

DROP TABLE IF EXISTS `ratio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ratio` (
  `rt_id` int(11) NOT NULL AUTO_INCREMENT,
  `rt_client_id` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `rt_entry_time` datetime NOT NULL,
  `rt_rate_one` float(10,2) DEFAULT NULL,
  `rt_rate_two` float(10,2) DEFAULT NULL,
  `rt_total_count` int(11) NOT NULL,
  `rt_local_ip` varchar(15) COLLATE utf8_unicode_ci NOT NULL,
  `rt_public_ip` varchar(15) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`rt_id`),
  KEY `entry_time` (`rt_entry_time`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'promisdb'
--
/*!50106 SET @save_time_zone= @@TIME_ZONE */ ;
/*!50106 DROP EVENT IF EXISTS `Avgs_Scheduler` */;
DELIMITER ;;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;;
/*!50003 SET character_set_client  = utf8 */ ;;
/*!50003 SET character_set_results = utf8 */ ;;
/*!50003 SET collation_connection  = utf8_general_ci */ ;;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = 'SYSTEM' */ ;;
/*!50106 CREATE*/ /*!50117 DEFINER=`root`@`localhost`*/ /*!50106 EVENT `Avgs_Scheduler` ON SCHEDULE EVERY 30 SECOND STARTS '2015-01-27 14:46:45' ON COMPLETION NOT PRESERVE ENABLE DO insert into averages(avg_ratio1,avg_ratio2,sum_total_count,avg_time,clients) select avg(rt_rate_one), avg(rt_rate_two), sum(rt_total_count),now(),count(distinct(rt_client_id)) from ratio where rt_entry_time <= now() and 
rt_entry_time>subtime(now(),'0:0:30') */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
/*!50106 DROP EVENT IF EXISTS `IP_Scheduler` */;;
DELIMITER ;;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;;
/*!50003 SET character_set_client  = utf8 */ ;;
/*!50003 SET character_set_results = utf8 */ ;;
/*!50003 SET collation_connection  = utf8_general_ci */ ;;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = 'SYSTEM' */ ;;
/*!50106 CREATE*/ /*!50117 DEFINER=`root`@`localhost`*/ /*!50106 EVENT `IP_Scheduler` ON SCHEDULE EVERY 1 HOUR STARTS '2015-07-24 16:46:07' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
DELETE FROM localIPs;
INSERT INTO localIPs(lclip_ip) SELECT DISTINCT(srcip) FROM packets WHERE srcip LIKE '150.140.90.101' or srcip LIKE '192.168%' OR srcip LIKE '127.0%' OR srcip LIKE '0.0%' OR srcip LIKE '10.%' OR srcip LIKE '172.1%' OR srcip LIKE '172.2%' OR srcip LIKE '172.31.%' OR srcip LIKE '%::%';
END */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
DELIMITER ;
/*!50106 SET TIME_ZONE= @save_time_zone */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-03 23:12:10