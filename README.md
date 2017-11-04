# REST-Shiro-demo
A Shiro demo shows remote RESTful realms usage. 

## Overview
This Shiro demo shows how to realize remote authentication and authorization through roles and permissions through RESTful API, it contains two web apps:
- /shiroservlet provides security data access by responding HTTP POST request
- /shiroweb consumes those security data through integrating Shiro and a custom realm 

## Shiroservlet
The core of this project is to implement the function: `protected void doPost(HttpServletRequest request, HttpServletResponse response)` in file: `/shiroservlet/src/main/java/net/yuchen/shiroservlet/`[ShiroServlet.java](/shiroservlet/src/main/java/net/yuchen/shiroservlet/ShiroServlet.java)

The servlet response to two types of POST request:
- POST in JSON format with an object: `"method":"authc"` along with login username and password. 

    It will verify the login token and reply with an additional object: `"message":"success!"` if login success or `"message":"fail!"` otherwise.
    Test with curl command:
```bash
curl -l -H "Content-type: application/json" -X POST -d '{"method":"authc","username":"lucas","password":"123456"}' "http://localhost:8080/shiroservlet-1.0-SNAPSHOT/ShiroServlet"
```
- POST in JSON format with an object: `"method":"authz"` along with login username. 

    It will reply with two additional JSONArray objects: `"roles":[...]` and `"permissions":[...]` if username are verified.
    Test with curl command:
```bash
curl -l -H "Content-type: application/json" -X POST -d '{"method":"authz","username":"bob"}' "http://localhost:8080/shiroservlet-1.0-SNAPSHOT/ShiroServlet"
```

## Shiroweb
This project provides a set of web page integrated with Shiro as security manager. The core is the custom realm realizing in:  `/shiroweb/src/main/java/net/yuchen/shiro/realm/`[ShiroRealm.java](/shiroweb/src/main/java/net/yuchen/shiro/realm/ShiroRealm.java), which mainly implement two following functions:

- `doGetAuthenticationInfo` authenticate user by passing the token to shiroservlet.
- `doGetAuthorizationInfo` authorize user by requesting shiroservlet roles and permissions information according to username.

With roles and permissions given, the filtration of resources are controlled by Shiro config file:  `/shiroweb/src/main/resources/`[shiro-web.ini](/shiroweb/src/main/resources/shiro-web.ini)

## Test
After deploying the two web apps in tomcat, you can access the login page at:

http://localhost:8080/shiroweb-1.0-SNAPSHOT/login

you can login and test with three account:

| Username  | Password | Roles | Permissions |
| :----------: |:----------: |:----------: |:----------: |
| lucas  | 123456  | admin,teacher  | user:\*, teacher:\*  |
| alice  | 888888  | teacher  | student:\*  |
| bob    | 666666  |          |            |

## Shiro Classes
![Classes](/images/ShiroClass.jpg)
## Shiro Authentication & Authorization
![AA](/images/ShiroAA.jpg)
