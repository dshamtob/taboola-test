CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    added_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    added_by VARCHAR(100)
);

CREATE TABLE product_price (
    product_id INT PRIMARY KEY,
    price DECIMAL(10, 2) NOT NULL,
    discount_percent INT DEFAULT 0,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE product_price_change_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    old_price DECIMAL(10, 2),
    new_price DECIMAL(10, 2),
    old_discount_percent INT,
    new_discount_percent INT,
    operation_type ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    performed_by VARCHAR(100),
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE ON UPDATE CASCADE
);


DELIMITER //
CREATE TRIGGER save_log_update
BEFORE UPDATE ON product_price
FOR EACH ROW
BEGIN
    INSERT INTO product_price_change_log (product_id, old_price, new_price, old_discount_percent, new_discount_percent, operation_type, operation_time, performed_by)
    VALUES (NEW.product_id, OLD.price, NEW.price, OLD.discount_percent, NEW.discount_percent, 'UPDATE', NEW.updated_time, NEW.updated_by);
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER save_log_insert
AFTER INSERT ON product_price
FOR EACH ROW
BEGIN
    INSERT INTO product_price_change_log (product_id, new_price, new_discount_percent, operation_type, operation_time, performed_by)
    VALUES (NEW.product_id, NEW.price, NEW.discount_percent, 'INSERT', NEW.updated_time, NEW.updated_by);
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER save_log_delete
BEFORE DELETE ON product_price
FOR EACH ROW
BEGIN
    INSERT INTO product_price_change_log (product_id, new_price, new_discount_percent, operation_type, operation_time, performed_by)
    VALUES (OLD.product_id, OLD.price, OLD.discount_percent, 'DELETE', CURRENT_TIMESTAMP, CURRENT_USER());
END;
//
DELIMITER ;

SELECT 
    p.name,
    p.category,
    pp.price,
    pp.updated_time,
    pp.updated_by
FROM 
    product p
JOIN 
    product_price pp ON p.product_id = pp.product_id;