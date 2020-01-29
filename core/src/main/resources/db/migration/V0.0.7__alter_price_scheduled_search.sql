ALTER TABLE `scheduledsearch` 
DROP `price`,
ADD `minPrice` int(11) NOT NULL,
ADD `maxPrice` int(11) NOT NULL;