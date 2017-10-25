<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/login" method="post">
        Username:<input type="text" name="userName"><br>
        Password:<input type="password" name="password"><br>
        <input type="submit" value="Submit">${errorInfo}
    </form>
</body>
</html>
