Για να λειτουργήσει η promisdb.sql χρειάζεται η ενεργοποίηση του event scheduler της MySQL

sudo nano /etc/mysql/my.cnf
.
.
.

[mysqld]
event_scheduler = ON  
.
.
.

ΤΟ db.sql περιέχει όλη τη βάση μαζί με τους schedulers.
Το scheduler.sql είναι μόνο οι schedulers κυρίως για reference.
