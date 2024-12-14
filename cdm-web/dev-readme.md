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
9. Try to send POST request on `http://localhost:8080/registration`. In postman, in the body of request you need to choose `x-www-form-urlencoded`, and choose `username`, `password` and `passwordConfirm` as keys and write your values
10. Upon a successful completion, request should return `201 created`
11. Then you can check in DB by double-clicking on `cdm_users` table
12. Go on `http://localhost:8080/login`, and try to log in
13. If login and password are correct, you will be redirected to `/home` page, which will have a `create container` button
14. If you put on this button, you will get a link for your container



### Guide for checking containers with Volumes on Windows



If you do not have a local version of the POSIX system, such as Ubuntu, volumes may become deprecated because the access rights for the directory on the container will be the same as the local `/data/{user}` directory on a Windows system. So they are converted to the POSIX standard mask `drwxr-xr-x` from the `root` user and you will not be able to create files in the container. This is because of the WSL standard mask for files. Therefore, you should have a local terminal similar to Ubuntu.
On it you should have root privileges. In Windows PowerShell, you can use the following command:
1. Command
   ```
   ubuntu config --default-user root
   ```
Otherwise, to resolve this issue, follow these steps:
1. Open a terminal on your POSIX-based operating system and follow the instructions below to install JDK if you do not already have it. (you may need to change the command for the JDK (SDK) you use to run your API in Idea):
   ```
   wget -O- https://apt.corretto.aws/corretto.key | sudo gpg --dearmor -o /usr/share/keyrings/corretto.gpg
   echo "deb [signed-by=/usr/share/keyrings/corretto.gpg] https://apt.corretto.aws stable main" | sudo tee /etc/apt/sources.list.d/corretto.list
   sudo apt update
   sudo apt install -y java-17-amazon-corretto-jdk
   java --version
   ```
2. Then go to the `Configuration` section near the `Run` and `Debug` button and edit it.
3. In `Run on` use `Manage targets` and add your POSIX OS as a target - for me, it was `WSL Ubuntu`
4. If everything is correct, the Idea will automatically find your JDK. Otherwise, you should change the `JDK home path`, `JDK version` and `java` in the build and run in the Configuration.
5. In settings of docker-desktop mark `Use the WSL 2 based engine` and `Expose daemon on tcp://localhost:2375 without TLS`
6. Run your application with `Run` button.