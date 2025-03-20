DELETE
FROM leave_request
WHERE schedule_id = 1;
DELETE
FROM shift
WHERE schedule_id = 1;
DELETE
FROM schedule
WHERE schedule_id = 1;
DELETE
FROM users
WHERE user_id BETWEEN 1 AND 20;
DELETE
FROM store
WHERE store_id = 1;