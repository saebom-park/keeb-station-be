-- category
insert into category (category_id, name, reg_time, update_time)
values (1, 'KEYBOARD', now(), now());

-- product
insert into product (product_id, category_id, name, description, base_price, status, reg_time, update_time)
values (1, 1, 'Test Keyboard', 'Test Product', 100000, 'ACTIVE', now(), now());

-- product_option
insert into product_option (product_option_id, product_id, option_summary, extra_price, status, is_default, reg_time, update_time)
values
    (1, 1, 'Black Switch', 0, 'AVAILABLE', true, now(), now()),
    (2, 1, 'Red Switch', 5000, 'AVAILABLE', false, now(), now());

-- stock
insert into stock (stock_id, product_option_id, quantity, version, reg_time, update_time)
values
    (1, 1, 10, 0, now(), now()),
    (2, 2, 5, 0, now(), now());