CREATE TABLE `scheduledsearchtask` (
  `id` varchar(255) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `executionTime` datetime NOT NULL,
  `state` varchar(255) NOT NULL,
  `service` varchar(255) NOT NULL,
  `scheduledSearch` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKh8nc5xdv2o92b91allerwo9ov` FOREIGN KEY (`scheduledSearch`) REFERENCES `scheduledsearch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
