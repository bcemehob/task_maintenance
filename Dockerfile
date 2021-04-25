FROM ubuntu:20.04

# PostgreSQL
RUN apt-get update && apt-get install -y gnupg2 && apt-get install -y tzdata
RUN apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8
RUN apt update
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ `lsb_release -cs`-pgdg main" |tee  /etc/apt/sources.list.d/pgdg.list
RUN apt-get install -y postgresql-12 postgresql-client-12
USER postgres
RUN    /etc/init.d/postgresql start &&\
    psql --command "CREATE USER taskapp_admin WITH SUPERUSER PASSWORD 'taskapp_admin';" &&\
    createdb -O taskapp_admin taskapp
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/12/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/12/main/postgresql.conf
EXPOSE 5432
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]
CMD ["/usr/lib/postgresql/12/bin/postgres", "-D", "/var/lib/postgresql/12/main", "-c", "config_file=/etc/postgresql/12/main/postgresql.conf"]
