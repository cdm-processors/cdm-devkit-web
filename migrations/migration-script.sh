#!bin/bash

for i in {1..20}; do
    pg_isready -h $POSTGRES_HOST -p $POSTGRES_PORT -U $POSTGRES_USER -d $POSTGRES_DB >/dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "Postgres is up - executing command"

        for f in /migrations/*.sql; do
            cat f
            PGPASSWORD=$POSTGRES_PASSWORD psql -h $POSTGRES_HOST -p $POSTGRES_PORT -U $POSTGRES_USER -d $POSTGRES_DB -f "$f"
            if [ $? -ne 0 ]; then
                echo "Error executing $f"
                exit 1
            fi
        done
        exit 0
    fi
    echo "Postgres is not up yet - sleeping"
    sleep 2
done

echo "Postgres is not up - exiting"
exit 1
