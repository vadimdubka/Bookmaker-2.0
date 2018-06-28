-- -----------------------------------------------------
-- Schema bookmaker
-- -----------------------------------------------------
-- Предметная область - букмекерская компания, позволяющая игрокам делать ставки на спортивные события онлайн.
--
-- Описание функционала:
-- Систему может использовать 3 вида пользователей: администратор, аналитик и игрок, - каждый из которых имеет свой функционал.
--
-- Игрок:
-- - регистрируется в системе.
-- - делает денежные ставки на различные типы исходов спортивных событий (победа, ничья, точный результат и др.), отслеживает результаты по своим ставкам.
-- - подает заявку на верификацию своего аккаунта, что позволит ему снимать денежные средства со счета.
-- - снимает и вносит денежные средства на свой счет.
-- - обладает статусом, от которого зависят его лимиты по ставкам и месячному выводу денежных средств со счета.
--
-- Администратор:
-- - управляет пользователями: изменяет роли пользователей, верифицирует аккаунты игроков на основании присланной игроком фотографии паспорта, изменяет статус игроков.
-- - управляет спортивными событиями и категориями событий: добавляет, изменяет, удаляет их.
-- - вносит в систему результаты событий.
-- - переводит на счет пользователей выигранные денежные средства по выигравшим ставкам.
--
-- Аналитик:
-- - определяет коэффициент доходности на исходы событий и вносит эту информацию в систему.

-- -----------------------------------------------------
-- Schema bookmaker
--
-- Предметная область - букмекерская компания, позволяющая игрокам делать ставки на спортивные события онлайн.
--
-- Описание функционала:
-- Систему может использовать 3 вида пользователей: администратор, аналитик и игрок, - каждый из которых имеет свой функционал.
--
-- Игрок:
-- - регистрируется в системе.
-- - делает денежные ставки на различные типы исходов спортивных событий (победа, ничья, точный результат и др.), отслеживает результаты по своим ставкам.
-- - подает заявку на верификацию своего аккаунта, что позволит ему снимать денежные средства со счета.
-- - снимает и вносит денежные средства на свой счет.
-- - обладает статусом, от которого зависят его лимиты по ставкам и месячному выводу денежных средств со счета.
--
-- Администратор:
-- - управляет пользователями: изменяет роли пользователей, верифицирует аккаунты игроков на основании присланной игроком фотографии паспорта, изменяет статус игроков.
-- - управляет спортивными событиями и категориями событий: добавляет, изменяет, удаляет их.
-- - вносит в систему результаты событий.
-- - переводит на счет пользователей выигранные денежные средства по выигравшим ставкам.
--
-- Аналитик:
-- - определяет коэффициент доходности на исходы событий и вносит эту информацию в систему.
-- -----------------------------------------------------
DROP TABLE IF EXISTS bet;
DROP TYPE IF EXISTS bet_status;
DROP TABLE IF EXISTS outcome;
DROP TABLE IF EXISTS outcome_type;
DROP TABLE IF EXISTS event;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS "transaction";
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS "user";
DROP TYPE IF EXISTS role;
DROP TABLE IF EXISTS player_status;
DROP TYPE IF EXISTS player_status_enum;
DROP TYPE IF EXISTS verification_status;


-- -----------------------------------------------------
-- Table `bookmaker`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS category (
  id        serial PRIMARY KEY,
  name      varchar(150) NOT NULL,
  parent_id integer REFERENCES category (id) ON DELETE CASCADE ON UPDATE CASCADE
);

COMMENT ON TABLE category
IS 'Категория, в рамках которого существует событие, например, вид спорта или чемпионат по какому-либо спорту.\nКатегории могут иметь древовидную структуру, т.е. в рамках спорта могут проводится различные чемпионаты, в рамках чемпионата могут выделяться различные стадии';
COMMENT ON COLUMN category.id
IS 'Id категории';
COMMENT ON COLUMN category.name
IS 'Наименование категории';
COMMENT ON COLUMN category.parent_id
IS 'Id родительской категории для создания древовидной структуры';

-- -----------------------------------------------------
-- Table `bookmaker`.`event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS event (
  id           serial PRIMARY KEY,
  category_id  integer      NOT NULL REFERENCES category (id) ON DELETE CASCADE ON UPDATE CASCADE,
  date         timestamp    NOT NULL,
  participant1 VARCHAR(150) NOT NULL,
  participant2 VARCHAR(150),
  result1      VARCHAR,
  result2      VARCHAR
);

COMMENT ON TABLE event
IS 'Событие в мире спорта, на варианты исхода которого букмекерская контора принимает ставки.';
COMMENT ON COLUMN event.id
IS 'Id события';
COMMENT ON COLUMN event.category_id
IS 'Id категории, к которой относится событие';
COMMENT ON COLUMN event.date
IS 'Дата и время начала события';
COMMENT ON COLUMN event.participant1
IS 'Описание первого или единственного участника события';
COMMENT ON COLUMN event.participant2
IS 'Описание второго участника события';
COMMENT ON COLUMN event.result1
IS 'Результат первого участника';
COMMENT ON COLUMN event.result2
IS 'Результат второго участника';

-- -----------------------------------------------------
-- Table `bookmaker`.`outcome_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS outcome_type (
  type        VARCHAR(5) PRIMARY KEY,
  description VARCHAR(45)
);

COMMENT ON TABLE outcome_type
IS 'Возможные типы исходов, которые могут иметь события, например, победа первого участника, ничья, количество забитых голов и т.д.';
COMMENT ON COLUMN outcome_type.type
IS 'Идентификатор типа исхода события в виде его краткого обозначения, например, 1 - победа первого участника, X - ничья.';
COMMENT ON COLUMN outcome_type.description
IS 'Текстовое описание типа исхода события.';

-- -----------------------------------------------------
-- Table `bookmaker`.`outcome`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS outcome (
  event_id    integer REFERENCES event (id) ON DELETE CASCADE ON UPDATE CASCADE,
  type        VARCHAR(5) REFERENCES outcome_type (type) ON DELETE RESTRICT ON UPDATE CASCADE,
  coefficient DECIMAL(5, 2) NOT NULL,
  PRIMARY KEY (event_id, type)
);

COMMENT ON TABLE outcome
IS 'Исход (результат) события, для которого букмекер рассчитал коэффициент доходности,  и на которое букмекерская компания принимает ставки от игроков.';
COMMENT ON COLUMN outcome.event_id
IS 'Id предстоящего события, для которого определяется вид исхода.';
COMMENT ON COLUMN outcome.type
IS 'Тип исхода, приминимого к событию.';
COMMENT ON COLUMN outcome.coefficient
IS 'Коэффициент доходности, используемый для расчета размера выигрыша игрока, который букмекер устанавливает на соответствующий тип исхода предстоящего события.';

-- -----------------------------------------------------
-- Table `bookmaker`.`player_status`
-- -----------------------------------------------------
CREATE TYPE player_status_enum AS ENUM ('unverified', 'basic', 'vip', 'ban');
CREATE TABLE IF NOT EXISTS player_status (
  status           player_status_enum,
  bet_limit        DECIMAL(5, 2) NOT NULL,
  withdrawal_limit DECIMAL(9, 2) NOT NULL,
  PRIMARY KEY (status)
);

COMMENT ON TABLE player_status
IS 'Перечисление статусов игроков, влияющий на лимиты по ставкам и выводу денежных средств со счета.';
COMMENT ON COLUMN player_status.status
IS 'Статусы игроков:\n- `unverified` - присваивается при регистрации новым игрокам. Статус позволяет делать ставки, но запрещает выводить деньги.\n- `basic` - присваивается после прохождения верификации. Статус позволяет выводить деньги.\n- ‘vip’ - имеет более высокие лимиты на ставки и вывод денег;\n- ‘ban’ - запрет на возможность делать ставки и выводить деньги.';
COMMENT ON COLUMN player_status.bet_limit
IS 'Размер максимальной возможной ставки.';
COMMENT ON COLUMN player_status.withdrawal_limit
IS 'Размер максимальной суммы выведенных средств в месяц.';

-- -----------------------------------------------------
-- Table `bookmaker`.`user`
-- -----------------------------------------------------`
CREATE TYPE role AS ENUM ('player', 'admin', 'analyst');
CREATE TABLE IF NOT EXISTS "user" (
  id                serial PRIMARY KEY,
  email             VARCHAR(320) NOT NULL,
  password          CHAR(32)     NOT NULL,
  role              role         NOT NULL DEFAULT 'player',
  registration_date timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX email_UNIQUE
  on "user" (email ASC);

COMMENT ON TABLE "user"
IS 'Зарегистрированный пользователь системы.';
COMMENT ON COLUMN "user".id
IS 'Id пользователя.';
COMMENT ON COLUMN "user".email
IS 'E-mail пользователя, используемый для входа в систему в качестве логина.\n\nМаксимальная длина email-адреса - 320 символов = 64(local-part)+1(@)+255(domain name).';
COMMENT ON COLUMN "user".password
IS 'Пароль пользователя, зашифрованный MD5.';
COMMENT ON COLUMN "user".role
IS 'Роль пользователя: игрок, администратор или аналитик.';
COMMENT ON COLUMN "user".registration_date
IS 'Дата регистрации пользователя.';
COMMENT ON INDEX email_UNIQUE
IS 'Индекс для ускорения поиска пользователя по его email.';

-- -----------------------------------------------------
-- Table `bookmaker`.`player`
-- -----------------------------------------------------
CREATE TYPE verification_status AS ENUM ('unverified', 'request', 'verified');
CREATE TABLE IF NOT EXISTS player (
  id                  serial PRIMARY KEY REFERENCES "user" (id) ON DELETE CASCADE ON UPDATE CASCADE,
  player_status       player_status_enum              DEFAULT 'unverified' REFERENCES player_status (status) ON DELETE NO ACTION ON UPDATE NO ACTION,
  balance             DECIMAL(12, 2)      DEFAULT '10.00',
  fname               VARCHAR(70),
  mname               VARCHAR(70),
  lname               VARCHAR(70),
  birthday            DATE,
  verification_status verification_status DEFAULT 'unverified',
  passport            VARCHAR(45),
  photo               bytea
);

COMMENT ON TABLE player
IS 'Игрок, делающий ставки на исходы событий.\nТаблица \"player\" создана со связью \"1-1\" к таблице \"user\", т.к.:\n1)  Часть данных в таблице \"player\" специфична только для игроков, например, свойство \"player_status\" нельзя применить к пользователям с ролью администратор. Т.е. можно рассматривать игрока и пользователя как две отдельные, хоть и взаимосвязанные через наследование сущности.\n2) Если таблицы не разделять, то будет нарушаться 3 нормальная форма, т.к. значение свойства \"player_status\" зависило бы не только от первичного ключа, но и от значения свойства \"role\".';
COMMENT ON COLUMN player.id
IS 'Id игрока.';
COMMENT ON COLUMN player.player_status
IS 'Стутус игрока.';
COMMENT ON COLUMN player.balance
IS 'Размер денежных средств на счету у игрока.';
COMMENT ON COLUMN player.fname
IS 'Имя игрока.';
COMMENT ON COLUMN player.mname
IS 'Отчество игрока.';
COMMENT ON COLUMN player.lname
IS 'Фамилия игрока.';
COMMENT ON COLUMN player.birthday
IS 'Дата рождения игрока. Может применяться для подтверждения, что игроку уже есть 18+ лет.';
COMMENT ON COLUMN player.verification_status
IS 'Статус верификации пользователя.\n `unverified` - неверифицированные пользователи.\n `request` - пользователь загрузил фотографию и подал заявку на верификацию.\n\ `verified` - администратор сверил данные и верифицировал игрока.';
COMMENT ON COLUMN player.passport
IS 'Идентификационный номер паспорта игрока.Необходимо для верификации пользователя.';
COMMENT ON COLUMN player.photo
IS 'Фото паспорта игрока.Необходимо для верификации пользователя.\nИгрок загружает фото паспорта, с помощью которого администратор  может верифицировать игрока и поменять его статус.';

CREATE TYPE bet_status AS ENUM ('new', 'losing', 'win', 'paid');
-- -----------------------------------------------------
-- Table `bookmaker`.`bet`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS bet (
  player_id   integer       NOT NULL REFERENCES player (id) ON DELETE CASCADE ON UPDATE CASCADE,
  event_id    integer       NOT NULL,
  type        VARCHAR(5)    NOT NULL,
  date        timestamp  DEFAULT CURRENT_TIMESTAMP,
  coefficient DECIMAL(5, 2) NOT NULL,
  amount      DECIMAL(9, 2) NOT NULL,
  status      bet_status DEFAULT 'new',
  PRIMARY KEY (player_id, event_id, type),
  FOREIGN KEY (event_id, type) REFERENCES outcome (event_id, type) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE bet
IS 'Ставка, сделанная игроком на исход события.';
COMMENT ON COLUMN bet.player_id
IS 'Id игрока, сделавшего ставку.';
COMMENT ON COLUMN bet.event_id
IS 'Id события, по которому игрок сделал ставку.';
COMMENT ON COLUMN bet.type
IS 'Тип исхода события, по которому игрок сделал ставку.';
COMMENT ON COLUMN bet.date
IS 'Дата и время, когда игрок сделал ставку..';
COMMENT ON COLUMN bet.coefficient
IS 'Коэффициент доходности на момент ставки игрока.';
COMMENT ON COLUMN bet.amount
IS 'Размер ставки игрока.';
COMMENT ON COLUMN bet.status
IS 'Статус ставки.\n `new` - игрок сделал новую ставку на исход события.\n `losing` - результаты события известны и ставка проиграла.\n `win` -  результаты события известны и ставка выиграла, но еще не оплачена букмекерской конторой.\n\ `paid` - выигравшая ставка оплачена.';

-- -----------------------------------------------------
-- Table `bookmaker`.`transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "transaction" (
  id        serial PRIMARY KEY,
  player_id integer       NOT NULL REFERENCES player (id) ON DELETE CASCADE ON UPDATE CASCADE,
  date      timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  amount    DECIMAL(9, 2) NOT NULL
);
CREATE INDEX fk_transaction_player1_idx
  ON transaction (player_id);
COMMENT ON TABLE transaction
IS 'Операция по снятию или зачилению денежных средств на счет игрока.';
COMMENT ON COLUMN transaction.id
IS 'Id операции со счетом.';
COMMENT ON COLUMN transaction.player_id
IS 'Id игрока.';
COMMENT ON COLUMN transaction.date
IS 'Дата операции со счетом.';
COMMENT ON COLUMN transaction.amount
IS 'Сумма по операции со счетом.';