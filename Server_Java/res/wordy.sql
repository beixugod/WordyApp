-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 27, 2023 at 08:03 AM
-- Server version: 8.0.31
-- PHP Version: 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `wordy`
--

-- --------------------------------------------------------

--
-- Table structure for table `game_session`
--

DROP TABLE IF EXISTS `game_session`;
CREATE TABLE IF NOT EXISTS `game_session` (
  `roomName` varchar(13) NOT NULL,
  `userID` bigint NOT NULL,
  `pointsAchieved` int DEFAULT NULL,
  `longestWord` varchar(40) DEFAULT NULL,
  `randomLetters` varchar(17) DEFAULT NULL,
  KEY `userID_fk` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `leaderboardwords`
--

DROP TABLE IF EXISTS `leaderboardwords`;
CREATE TABLE IF NOT EXISTS `leaderboardwords` (
  `word` varchar(255) DEFAULT NULL,
  `leadID` int NOT NULL AUTO_INCREMENT,
  `userID` bigint DEFAULT NULL,
  PRIMARY KEY (`leadID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `leaderboardwords`
--

INSERT INTO `leaderboardwords` (`word`, `leadID`, `userID`) VALUES
('abattoir', 1, 1),
('abattoirs', 2, 1),
('abandonment', 3, 1),
('abandonment', 4, 2),
('abatement', 5, 4),
('abbreviate', 6, 5),
('abdicated', 7, 7);

-- --------------------------------------------------------

--
-- Table structure for table `topusers`
--

DROP TABLE IF EXISTS `topusers`;
CREATE TABLE IF NOT EXISTS `topusers` (
  `userID` bigint NOT NULL,
  `matchesWon` int NOT NULL,
  UNIQUE KEY `userID` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `topusers`
--

INSERT INTO `topusers` (`userID`, `matchesWon`) VALUES
(1, 5),
(2, 5);

-- --------------------------------------------------------

--
-- Table structure for table `userleaderboard`
--

DROP TABLE IF EXISTS `userleaderboard`;
CREATE TABLE IF NOT EXISTS `userleaderboard` (
  `uleadID` int NOT NULL AUTO_INCREMENT,
  `userID` bigint NOT NULL,
  `matchesWon` int NOT NULL,
  PRIMARY KEY (`uleadID`),
  UNIQUE KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `userleaderboard`
--

INSERT INTO `userleaderboard` (`uleadID`, `userID`, `matchesWon`) VALUES
(1, 1, 9),
(2, 2, 9),
(3, 3, 7),
(4, 4, 8),
(5, 5, 4),
(6, 8, 6);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `userID` bigint NOT NULL AUTO_INCREMENT,
  `userName` text NOT NULL,
  `password` varchar(10) NOT NULL,
  `status` varchar(10) NOT NULL DEFAULT 'OFFLINE',
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`userID`, `userName`, `password`, `status`) VALUES
(1, 'rose11', '123', 'OFFLINE'),
(2, 'coolkidx', '123', 'OFFLINE'),
(3, 'newname', '123', 'OFFLINE'),
(4, 'suja', '123', 'OFFLINE'),
(5, 'roger', '123', 'OFFLINE'),
(6, 'axel', '123', 'OFFLINE'),
(7, 'zydex', '123', 'OFFLINE'),
(8, 'sneak', '123', 'OFFLINE');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `game_session`
--
ALTER TABLE `game_session`
  ADD CONSTRAINT `userID_fk` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `leaderboardwords`
--
ALTER TABLE `leaderboardwords`
  ADD CONSTRAINT `leaderboardwords_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `topusers`
--
ALTER TABLE `topusers`
  ADD CONSTRAINT `topusers_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `userleaderboard`
--
ALTER TABLE `userleaderboard`
  ADD CONSTRAINT `userleaderboard_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
