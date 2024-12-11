### Guide for launching CdM-Web Spring application



Steps:
1. Copy the .env file in the same directory with compose.yaml (it contains container's database sensitive data)
   * Note: if you see in this guide something like `${POSTGRES_DB}`, it means that you need to use corresponding value from .env file
2. Install (if you haven't done it): 
    * database plugin for IntelliJ IDEA
    * Docker Desktop (IMPORTANT: you have to turn on `Settings` -> `General` -> `Expose daemon on tcp://localhost:2375 without TLS`)
3. Run CdmWebApplication.java
4. Check if container with application runned
5. Then, in IntelliJ IDEA: `View` -> `Tool Windows` -> `Database`
6. Select PostgreSQL as data source and add `${POSTGRES_DB}` with all schemas
7. In database tool window, you can test connection by pressing "Test connection"
8. If connection is OK, in the same tool window open query console and run this query:
   ```sql
   INSERT INTO public.t_roles(id, name)
   VALUES (1, 'ROLE_USER'), (2, 'ROLE_ADMIN');
   ```
   There can be a problem, that console doesn't see 't_roles' table; in this case you have to put a cursor on table name and IDEA suggests you to choose a schema: you need to choose `${POSTGRES_DB}` schema
9. Try to send POST request on `http://localhost:8080/registration`. In postman, in the body of request you need to choose `x-www-form-undecoded`, and choose `username`, `password` and `passwordConfirm` as keys and write your values
10. Upon a successful completion, request should return `201 created`
11. Then you can check in DB by double-clicking on `cdm_users` table
12. Go on `http://localhost:8080/login`, and try to log in
13. If login and password are correct, you will be redirected to `/home` page, which will have a `create container` button
14. If you put on this button, you will get a link for your container