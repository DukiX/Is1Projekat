CREATE DATABASE  IF NOT EXISTS `projekatis1` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `projekatis1`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: projekatis1
-- ------------------------------------------------------
-- Server version	5.6.43

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
-- Table structure for table `alarmi`
--

DROP TABLE IF EXISTS `alarmi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alarmi` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `AKTIVAN` tinyint(1) DEFAULT '0',
  `DATUMALARMA` date DEFAULT NULL,
  `PERIODICAN` tinyint(1) DEFAULT '0',
  `VREMEALARMA` time DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarmi`
--

LOCK TABLES `alarmi` WRITE;
/*!40000 ALTER TABLE `alarmi` DISABLE KEYS */;
INSERT INTO `alarmi` VALUES (1,0,'2019-05-25',0,'13:13:00'),(2,0,'2019-05-21',0,'12:00:00'),(3,0,'2019-05-21',0,'12:02:00'),(4,0,'2019-06-15',0,'13:32:00'),(5,1,'2019-11-22',0,'19:42:00'),(6,1,'2019-07-15',0,'09:00:00'),(7,0,'2019-09-01',0,'23:32:00'),(8,0,'2019-06-23',0,'04:32:00'),(9,0,'2019-05-21',0,'14:58:00'),(10,0,'2019-05-21',0,'15:03:00'),(11,0,'2019-05-21',0,'15:09:00'),(12,0,'2019-05-21',0,'15:10:00'),(13,0,'2019-05-21',0,'15:11:00'),(14,0,'2019-05-21',0,'15:17:00'),(20,0,'2019-05-23',0,'12:23:00'),(21,1,'2019-12-15',0,'13:23:00'),(22,0,'2019-05-24',0,'12:00:00'),(23,0,'2019-05-24',0,'13:22:00'),(24,0,'2019-05-24',0,'12:00:00'),(25,0,'2019-05-25',0,'10:41:00'),(26,1,'2019-06-17',0,'07:27:00'),(27,0,'2019-07-11',0,'18:17:00'),(28,1,'2019-06-19',0,'18:08:00'),(29,0,'2019-06-03',0,'12:41:00'),(30,1,'2019-06-11',0,'15:00:00');
/*!40000 ALTER TABLE `alarmi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kalendar`
--

DROP TABLE IF EXISTS `kalendar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kalendar` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DATUM` date DEFAULT NULL,
  `DESTINACIJA` varchar(255) DEFAULT NULL,
  `OPIS` varchar(255) DEFAULT NULL,
  `PODSETNIK` tinyint(1) DEFAULT '0',
  `VREME` time DEFAULT NULL,
  `IDALARM` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_KALENDAR_IDALARM` (`IDALARM`),
  CONSTRAINT `FK_KALENDAR_IDALARM` FOREIGN KEY (`IDALARM`) REFERENCES `alarmi` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kalendar`
--

LOCK TABLES `kalendar` WRITE;
/*!40000 ALTER TABLE `kalendar` DISABLE KEYS */;
INSERT INTO `kalendar` VALUES (1,'2019-05-25','Pozega','izmenjena obaveza',1,'13:13:00',1),(2,'2019-05-21',NULL,'idi na faks',1,'12:00:00',2),(4,'2019-07-15',NULL,'daleko',1,'09:00:00',6),(7,'2019-05-21',NULL,'jos jednom',1,'14:58:00',9),(8,'2019-05-23',NULL,'za dodavanje alarma',0,'12:23:00',20),(10,'2019-12-15','Sabac','put u sabac',1,'13:23:00',21),(14,'2019-05-25','Subotica','distance test',1,'11:00:00',25),(15,'2019-06-17','Nis','idem u nis',1,'10:00:00',26),(17,'2019-06-20','London','jos jedan test',1,'15:00:00',28),(18,'2019-06-11',NULL,'Ispit iz isa',1,'15:00:00',30),(19,'2019-06-13',NULL,'ispit iz saba',0,'15:00:00',NULL);
/*!40000 ALTER TABLE `kalendar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pustenepesme`
--

DROP TABLE IF EXISTS `pustenepesme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pustenepesme` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAZIVPESME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pustenepesme`
--

LOCK TABLES `pustenepesme` WRITE;
/*!40000 ALTER TABLE `pustenepesme` DISABLE KEYS */;
INSERT INTO `pustenepesme` VALUES (1,'Rane'),(2,'Rane'),(3,'Rane'),(4,'Rane'),(5,'Rane'),(6,'Samo se nocas pojavi'),(7,'Samo se nocas pojavi'),(8,'Samo se nocas pojavi'),(9,'izvini se'),(10,'samo se nocas pojavi'),(11,'Neverna'),(12,'koza pamti'),(13,'sve je na prodaju'),(14,'Varnice'),(15,'varnice'),(16,'drska zeno plava'),(17,'prejako');
/*!40000 ALTER TABLE `pustenepesme` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zvonoalarma`
--

DROP TABLE IF EXISTS `zvonoalarma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zvonoalarma` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PESMA` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zvonoalarma`
--

LOCK TABLES `zvonoalarma` WRITE;
/*!40000 ALTER TABLE `zvonoalarma` DISABLE KEYS */;
INSERT INTO `zvonoalarma` VALUES (1,'varnice');
/*!40000 ALTER TABLE `zvonoalarma` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-06-05 17:33:32
