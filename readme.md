# The DashBoard

The DashBoard is hosted on Railway at https://smartwatch-production.up.railway.app/

# The API

The API is hosted on Railway at https://smartwatch-production.up.railway.app/api

## How to start the API Localy :

# Step 1

Start `Docker` and then run:

```bash
cd server
npm i
npm run docker:build  #Build images and containers
npm start
```

You should have the address of the api displayed in the console. Otherwise you will need to run `ipconfig` and look for your IPv4 address.

# Step 2

To be sure the server is running open the address you got in the previous step in your browser (it should be something like http://192.168.0.134:3000) and you should see the dashboard.

# Step 3

Open the folder `application` with `Android Studio` and then open `app/build.gradle`. You might need to change the address in the line:

> buildConfigField "String", "API_URL", "\\"https<span>://smartwatch-production.up.railway.app/api\\""

with the ip address you got in step 1.

# Step 4

You can run the application from `Android Studio` in the emulator and everything should be working as intented.

# TroubleShooting

> ERROR 2002 (HY000): Can't connect to local server through socket '/run/mysqld/mysqld.sock' (2)

This error mean that the mysql container is not ready yet and you should wait about 10s.

#

> error during connect: this error may indicate that the docker daemon is not running: Get "http://%2F%2F.%2Fpipe%2Fdocker_engine/v1.24/containers/json?all=1&filters=%7B%22label%22%3A%7B%22com.docker.compose.project%3Dserver%22%3Atrue%7D%7D": open //./pipe/docker_engine: The system cannot find the file specified.

> Unable to connect to the database: SequelizeConnectionRefusedError: connect ECONNREFUSED 127.0.0.1:3306

These errors mean that docker in not running, to fix it simply start it.

#
