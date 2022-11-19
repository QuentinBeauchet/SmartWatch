# Step 1

Start `Docker` and then run:

```bash
cd server
npm start
```

You should have the address of the api displayed in the console. Otherwise you will need to run `ipconfig` and look for your IPv4 address.

# Step 2

To be sure the server is running open the address you got in the previous step in your browser (it should be something like http://192.168.0.134:3000) and you should see the dashboard.

# Step 3

Open the folder `application` with `Android Studio` and then open `app/build.gradle`. You might need to change the address in the line:

> buildConfigField "String", "API_URL", "\\"http://192.168.0.134:3000/api\""

with the ip address you got in step 1.

# Step 4

You can run the application from `Android Studio` in the emulator and everything should be working as intented.
