<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Access denied!</title>
    <style>
        body {
            background: url("../../images/noaccess.gif") no-repeat linen;
            background-size: contain;
        }
    </style>
    <link href="../images/Pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>
<img src="${pageContext.request.contextPath}/images/noaccess.gif" />

<%--<p3>You have not access for this page!</p3>--%>
<c:choose>
    <c:when test="${empty accountRole}">
        <a href="${pageContext.request.contextPath}/main"><br>Вернуться на главную<br><br></a>
    </c:when>
    <c:otherwise>
        <a href="${pageContext.request.contextPath}/${accountRole}/main"><br>Вернуться на главную<br><br></a>
    </c:otherwise>
</c:choose>
</body>
</html>


<%--${pageContext.request.contextPath}/--%>