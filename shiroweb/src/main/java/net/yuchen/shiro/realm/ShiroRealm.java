package net.yuchen.shiro.realm;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.SecurityUtils;
import java.net.URL;
import java.util.*;
import org.json.*;
import java.io.*;

public class ShiroRealm extends AuthorizingRealm {
    private String RestServerURL = "http://localhost:8080/shiroservlet-1.0-SNAPSHOT/ShiroServlet";
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        final String username;
        final String password;
        try {
            username = (String) authenticationToken.getPrincipal();
        } catch (final ClassCastException e) {
            System.out.println("doGetAuthenticationInfo() failed because the principal couldn't be cast as a String");
            throw e;
        }

        final UsernamePasswordToken upt;
        try {
            upt = (UsernamePasswordToken) authenticationToken;
        } catch (final ClassCastException e) {
            System.out.println("doGetAuthenticationInfo() failed because the token was not a UsernamePasswordToken");
            throw e;
        }

        password = new String(upt.getPassword());
        
        final String principal = RestAuthenticate(username,password);
        if (principal != null){
            return new SimpleAuthenticationInfo(principal, password,getName());
        } else {
            throw new AuthenticationException("bad login");
        }
        
    }
    private String RestAuthenticate(final String username, final String password) {
        final JSONTokener tokener;
        final JSONObject object;
        final String input = "{\"method\":\"authc\",\"username\":\""+ username + "\"," + "\"password\":" + "\"" + password + "\"}";
        final ClientConfig config = new DefaultClientConfig();
        final Client client = Client.create(config);
        final WebResource webResource = client.resource(RestServerURL);
        final ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
        final String output = response.getEntity(String.class);
        tokener = new JSONTokener(output);
        object = new JSONObject(tokener);
        String message;
        try {
            message = (String) object.get("message");
        } catch (NullPointerException e){
            return null;
        }
        if(message.equals("success!")){
            return username;
        }
        else { return null;}        
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) throws AuthenticationException{
        final String loginName = SecurityUtils.getSubject().getPrincipal().toString();
        if (loginName != null) {
            final SimpleAuthorizationInfo info = RestAuthorization(loginName);
         
            return info;
        }
        return null;
    }

    private SimpleAuthorizationInfo RestAuthorization(final String username) {
        final JSONTokener tokener;
        final JSONObject object;
        final String input = "{\"method\":\"authz\",\"username\":\""+ username + "\"}";
        final ClientConfig config = new DefaultClientConfig();
        final Client client = Client.create(config);
        final WebResource webResource = client.resource(RestServerURL);
        final ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
        final String output = response.getEntity(String.class);
        tokener = new JSONTokener(output);
        object = new JSONObject(tokener);

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        JSONArray roles;
        JSONArray permissions;
        Set<String> roleSet = new HashSet<String>();
        Set<String> permissionSet = new HashSet<String>();
        try {
            roles = object.getJSONArray("roles");
            permissions = object.getJSONArray("permissions");
        } catch (NullPointerException e){
            return null;
        }
        if(roles.length() != 0){
            for (int i = 0; i < roles.length(); i++){
                roleSet.add(roles.getString(i));
            }
            info.addRoles(roleSet);
        }
        if(permissions.length() != 0){
            for (int i = 0; i < permissions.length(); i++){
                permissionSet.add(permissions.getString(i));
            }
            info.addStringPermissions(permissionSet);
        }
        return info;
    }
}
