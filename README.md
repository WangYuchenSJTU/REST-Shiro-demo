# REST-Shiro-demo
A Shiro demo shows remote RESTful realms usage. 

## Overview
This Shiro demo shows how to realize remote authentication and authorization through roles and permissions through RESTful API, it contains two web apps:
- /shiroservlet provides security data access by responding HTTP POST request
- /shiroweb consumes those security data through integrating Shiro and a custom realm 

## Shiroservlet
This project mainly implement the function: `protected void doPost(HttpServletRequest request, HttpServletResponse response)` in file: `/shiroservlet/src/main/java/net/yuchen/shiroservlet/ShiroServlet.java`: [ShiroServlet.java](/shiroservlet/src/main/java/net/yuchen/shiroservlet/ShiroServlet.java)

The function response to two types of request:
- POST in JSON format with an object: `"method":"authc"` along with login username and password. 

    It will verify the login token and reply with an additional object: `"message":"success!"` if login success or `"message":"fail!"` otherwise.
```bash
curl -l -H "Content-type: application/json" -X POST -d '{"method":"authc","username":"lucas","password":"123456"}' "http://localhost:8080/shiroservlet/ShiroServlet"
```
- POST in JSON format with an object: `"method":"authz"` along with login username. 

    It will reply with two additional JSONArray objects: `"roles":[...]` and `"permissions":[...]` if username are verified.
```bash
curl -l -H "Content-type: application/json" -X POST -d '{"method":"authz","username":"bob"}' "http://localhost:8080/shiroservlet/ShiroServlet"
```
