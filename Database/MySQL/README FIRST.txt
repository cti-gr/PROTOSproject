��� �� ������������ � promisdb.sql ���������� � ������������ ��� event scheduler ��� MySQL

sudo nano /etc/mysql/my.cnf
.
.
.

[mysqld]
event_scheduler = ON  
.
.
.

�� db.sql �������� ��� �� ���� ���� �� ���� schedulers.
�� scheduler.sql ����� ���� �� schedulers ������ ��� reference.
