ALTER TABLE `flight` 
ADD `duration` int(11) NOT NULL,
MODIFY COLUMN `date` datetime NOT NULL;