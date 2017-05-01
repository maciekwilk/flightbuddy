CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jreodf78a7pl5qidfh43axdfb` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `flightbuddy`.`user` (`id`, `created`, `updated`, `version`, `enabled`, `password`, `username`) 
VALUES ('21eb2a80df9c493d9a26c709a9bda218', '2017-05-01 18:44:00', '2017-05-01 18:44:00', '0', b'1', 'password', 'maciekkwilk@gmail.com');
