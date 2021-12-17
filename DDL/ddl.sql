create database ewallet;

use ewallet;

create table users (
    username varchar(40) not null,
    token varchar(40) not null,
    create_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (username)
);
create index token_user on users(token);

create table user_balances (
    user_balance_id int not null AUTO_INCREMENT,
    username varchar(40) not null,
    balance numeric not null,
    last_update_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (user_balance_id),
    foreign key (username) references users(username)
);
alter table user_balances add unique (username);

create table transactions (
    username varchar(40) not null,
    transaction_type varchar(25) not null,
    transaction_id varchar(30) not null,
    ref_id varchar(30) not null,
    counter_party varchar(40) not null,
    create_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_date integer not null,
    side varchar(1) not null,
    amount numeric not null,
    primary key (username, transaction_type, transaction_id),
    foreign key (username) references users(username)
);

create table transaction_debit_summary (
    username varchar(40) not null,
    period_date integer not null,
    period_month integer not null,
    amount numeric not null,
    last_update_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (username, period_date),
    foreign key (username) references users(username)
);