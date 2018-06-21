<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Index</title>
</head>
<body>
<jsp:forward page="${pageContext.request.contextPath}/controller?command_type=goto_main"/>
</body>
</html>