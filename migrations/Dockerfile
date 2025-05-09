FROM postgres:latest

COPY ./* /migrations/

RUN chmod +x /migrations/migration-script.sh

CMD [ "bash", "-c", "/migrations/migration-script.sh" ]