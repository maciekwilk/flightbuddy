CREATE TABLE `scheduledsearch` (
  `id` varchar(255) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `from` varchar(255) NOT NULL,
  `price` decimal(19,2) NOT NULL,
  `to` varchar(255) NOT NULL,
  `withReturn` bit(1) NOT NULL,
  `user` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKh8nc5xdv2o92b91allqcwo9ov` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
