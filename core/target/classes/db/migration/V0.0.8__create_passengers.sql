CREATE TABLE `passengers` (
  `id` varchar(255) NOT NULL,
  `adultCount` int(11) NOT NULL,
  `childCount` int(11) NOT NULL,
  `infantInLapCount` int(11) NOT NULL,
  `infantInSeatCount` int(11) NOT NULL,
  `seniorCount` int(11) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `scheduledSearch` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKh8nc5xdv2o92b91relerwo9ov` FOREIGN KEY (`scheduledSearch`) REFERENCES `scheduledsearch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
