CREATE TABLE `user_role` (
  `user_id` varchar(255) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK250qsoghxra2h8tqpfildi605` (`role_id`),
  CONSTRAINT `FK250qsoghxra2h8tqpfildi605` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FKpamkaxhj7jlv9f3lpsis1r018` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `flightbuddy`.`user_role` (`user_id`, `role_id`) VALUES ('21eb2a80df9c493d9a26c709a9bda218', '1');
