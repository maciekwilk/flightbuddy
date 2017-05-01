CREATE TABLE `role` (
  `id` int NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `flightbuddy`.`role` (`id`, `role`) VALUES ('0', 'SYSTEM');
INSERT INTO `flightbuddy`.`role` (`id`, `role`) VALUES ('1', 'ADMIN');
INSERT INTO `flightbuddy`.`role` (`id`, `role`) VALUES ('2', 'USER');
