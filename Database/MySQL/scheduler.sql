DELIMITER $$
DROP EVENT IF EXISTS Avgs_Scheduler$$
CREATE EVENT `Avgs_Scheduler` 
ON SCHEDULE EVERY '30' SECOND
DO insert into averages(avg_ratio1,avg_ratio2,sum_total_count,avg_time,clients) select avg(rt_rate_one), avg(rt_rate_two), sum(rt_total_count),now(),count(distinct(rt_client_id)) from ratio where rt_entry_time <= now() and 
rt_entry_time>subtime(now(),'0:0:30')$$
DELIMITER ;

DELIMITER $$
DROP EVENT IF EXISTS IP_Scheduler$$
CREATE EVENT `IP_Scheduler` 
ON SCHEDULE EVERY '1' HOUR
DO 
BEGIN
DELETE FROM localIPs;
INSERT INTO localIPs(lclip_ip) SELECT DISTINCT(srcip) FROM packets WHERE srcip LIKE '150.140.90.101' or srcip LIKE '192.168%' OR srcip LIKE '127.0%' OR srcip LIKE '0.0%' OR srcip LIKE '10.%' OR srcip LIKE '172.1%' OR srcip LIKE '172.2%' OR srcip LIKE '172.31.%' OR srcip LIKE '%::%';
END$$
DELIMITER ;