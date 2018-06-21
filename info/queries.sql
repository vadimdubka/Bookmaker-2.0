-- 1 Запросы с WHERE
-- 1.1 Выбрать среди категорий событий те, которые являются видом спорта и корнем иерархической структуры категорий событий.
SELECT 
    `id`, `name`
FROM
    `category`
WHERE
    `parent_id` IS NULL;
    
-- 1.2 Выбрать категории событий для конкретного вида спорта.
SELECT 
    `id`, `name`, `parent_id`
FROM
    `category`
WHERE
    `parent_id` = 18;

-- 2 Запросы с встроенными функциями
-- 2.1 Выбрать события, которые еще не начались и по которым игрок может сделать ставку.
SELECT 
    `id`, `category_id`, `date`, `participant1`, `participant2`
FROM
    `event`
WHERE
    (`date` - NOW()) > 0 ORDER BY `category_id`;
    
-- 2.2 Определить, сколько дней конкретный пользователь зарегистрирован в системе.
SELECT 
    TIMESTAMPDIFF(DAY,
        (SELECT 
                `registration_date`
            FROM
                `user`
            WHERE
                `id` = 4), NOW()) AS `days`;
        
-- 2.3 Вставка записи при регистрации нового пользователя
INSERT INTO `user` (`email`, `password`, `role`, `registration_date`) 
VALUES (LOWER('volKov@mail.com'), MD5('volkov26Dk'), 'admin', NOW());

-- 3 Запросы с JOIN
-- 3.1 Выбрать всех пользователей системы с их полными именами и контактными данными для возможности администратору связаться с ними. 
SELECT 
    `id`,
    `role`,
    `email`,
    CONCAT_WS(' ', `fname`, `mname`, `lname`) AS `name`
FROM
    `user`
        LEFT JOIN `player` USING (`id`);
    
-- 3.2 Выбрать информацию о выигравших ставках, сделавших их игроках и сумме выигрыша игроков, который букмекерская компания обязана выплатить игрокам;
SELECT 
    `player_id`,
    `event_id`,
    CONCAT_WS(' ', `fname`, `mname`, `lname`) AS `player name`,
    CONCAT_WS(' - ', `participant1`, `participant2`) AS `event name`,
    `type` AS `bet type`,
    `amount` AS `bet amount`,
    `coefficient`,
    (`amount` * `coefficient`) AS `winning`
FROM
    `bet`
        JOIN `player` ON `bet`.`player_id` = `player`.`id`
        JOIN `outcome` USING (`type` , `event_id`)
        JOIN `event` ON `bet`.`event_id`=`event`.`id`
WHERE
    `status` = 'win';

-- 4 Запросы с GROUP BY, HAVING и агрегатными функциями
-- 4.1 Вывести количество игроков по каждому статусу.
SELECT 
    UPPER(`player_status`) AS `status`, 
    COUNT(*) AS `amount of players`
FROM
    `player`
GROUP BY `status`;

-- 4.2 Вывести помесячно сумму прибыли или убытка компании по транзакциям за текущий год.
SELECT 
    YEAR(`date`) AS `year`,
    MONTHNAME(`date`) AS `month`,
    SUM(`amount`) * (- 1) AS `benefit` #умножаем на -1, т.к. в таблице transaction доходы относительно компании считаются с минусом
FROM
    `transaction`
WHERE
    YEAR(`date`) = 2017
GROUP BY `month`
ORDER BY `date`;
-- 4.3 Вывести игроков, которые выигрывают у букмекера денег больше, чем ставят на ставки.
SELECT 
    `player`.`id`, 
    `lname`, 
    SUM(`amount`) AS `total winnings`
FROM
    `player`
        JOIN `transaction` ON `player`.`id` = `transaction`.`player_id`
GROUP BY `player_id`
HAVING `total winnings` > 0;

-- 5 Запрос с UNION
-- 5.1 Вывести отчет по доходам и расходам компании по игорной деятельности за текущий год.
SELECT 
    '2017 income' AS `year`, 
    SUM(`amount`) * (- 1) AS `total`
FROM
    `transaction`
WHERE
    YEAR(`date`) = 2017 AND `amount` < 0
UNION SELECT 
    '2017 expenses', 
    SUM(`amount`) * (- 1) AS `total`
FROM
    `transaction`
WHERE
    YEAR(`date`) = 2017 AND `amount` > 0;

-- 6 Запросы с подзапросами
-- 6.1 Невзаимосвязанный запрос. Показать ставки забаненных игроков, выигрыши по которым администратор должен аннулировать.
SELECT 
    `player_id`, `event_id`, `type`, `amount`, `status`
FROM
    `bet`
WHERE
    `status` = 'win' AND `player_id` IN (
        SELECT 
            `id`
        FROM
            `player`
        WHERE
            `player_status` = 'ban')
GROUP BY `player_id`;

-- 6.2 Взаимосвязанный запрос. Показать события, для которых аналитик еще не определил ни одного исхода события.
SELECT 
    `id`, `participant1`, `participant2`, `category_id`
FROM
    `event` 
WHERE
    NOT EXISTS( 
        SELECT 
            `event_id`
        FROM
            `outcome`
        WHERE
            `outcome`.`event_id` = `event`.`id`)
ORDER BY `category_id`;

SELECT id, date, category_id, participant1, participant2, result1, result2 FROM event WHERE id =33;