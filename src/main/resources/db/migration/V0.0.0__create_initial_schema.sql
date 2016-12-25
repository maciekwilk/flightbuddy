CREATE DATABASE  IF NOT EXISTS `flightbuddy` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `flightbuddy`;

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
-- Table structure for table `AIRLINE`
--

DROP TABLE IF EXISTS `AIRLINE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AIRLINE` (
  `ID` varchar(255) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `FLIGHT_ID` varchar(255) DEFAULT NULL,
  `CREATED` date NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_AIRLINE_FLIGHT_ID` (`FLIGHT_ID`),
  CONSTRAINT `FK_AIRLINE_FLIGHT_ID` FOREIGN KEY (`FLIGHT_ID`) REFERENCES `FLIGHT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FLIGHT`
--

DROP TABLE IF EXISTS `FLIGHT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FLIGHT` (
  `ID` varchar(255) NOT NULL,
  `DATE` date DEFAULT NULL,
  `FOUNDTRIP_ID` varchar(255) DEFAULT NULL,
  `CREATED` date NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_FLIGHT_FOUNDTRIP_ID` (`FOUNDTRIP_ID`),
  CONSTRAINT `FK_FLIGHT_FOUNDTRIP_ID` FOREIGN KEY (`FOUNDTRIP_ID`) REFERENCES `FOUNDTRIP` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FOUNDTRIP`
--

DROP TABLE IF EXISTS `FOUNDTRIP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FOUNDTRIP` (
  `ID` varchar(255) NOT NULL,
  `PRICE` decimal DEFAULT NULL,
  `CREATED` date NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `STOP`
--

DROP TABLE IF EXISTS `STOP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `STOP` (
  `ID` varchar(255) NOT NULL,
  `CODE` varchar(255) DEFAULT NULL,
  `FLIGHT_ID` varchar(255) DEFAULT NULL,
  `CREATED` date NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_STOP_FLIGHT_ID` (`FLIGHT_ID`),
  CONSTRAINT `FK_STOP_FLIGHT_ID` FOREIGN KEY (`FLIGHT_ID`) REFERENCES `FLIGHT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
