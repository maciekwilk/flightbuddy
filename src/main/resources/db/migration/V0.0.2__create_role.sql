CREATE TABLE `role` (
  `id` varchar(255) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  KEY `FKh8nc5xdv2o96b91allqcwo9ov` (`id`),
  CONSTRAINT `FKh8nc5xdv2o96b91allqcwo9ov` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
