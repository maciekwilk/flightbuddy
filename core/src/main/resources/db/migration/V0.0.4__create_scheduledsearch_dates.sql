CREATE TABLE `scheduledsearch_dates` (
  `id` varchar(255) NOT NULL,
  `dates` date NOT NULL,
  KEY `FKh8nc5xdv2o95b91allqcwo9ov` (`id`, `dates`),
  CONSTRAINT `FKh8nc5xdv2o94b91allqcwo9ov` FOREIGN KEY (`id`) REFERENCES `scheduledsearch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;