FROM postgres:latest

RUN apt-get update && apt-get install -y postgresql-client

COPY conf/postgresql.conf /etc/postgresql/postgresql.conf
COPY conf/pg_hba.conf /etc/postgresql/pg_hba.conf
COPY init/setup.sh /setup-replica.sh

RUN chmod +x /setup-replica.sh

CMD ["/setup-replica.sh"]