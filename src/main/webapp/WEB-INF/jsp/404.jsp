<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>On-line pharmacy. Page not found ((</title>
    <style>
        <%@include file="/css/style.css" %>
        body {
            background: url("${pageContext.request.contextPath}/images/nofound.gif") no-repeat fixed center center;
            left: 20%;
            background-size: contain;
            background-clip: border-box;
        }
    </style>
    <link href="${pageContext.request.contextPath}/images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>

<div class="div-left">
    <br>
    <br>
    <p class="p-big-left">Извините, <br>страница не найдена</p>
    <br>
    <br>
    <c:choose>
        <c:when test="${empty accountRole}">
            <a class="link-back" href="${pageContext.request.contextPath}/main">Вернуться на главную</a>
        </c:when>
        <c:otherwise>
            <a class="link-back" href="${pageContext.request.contextPath}/${accountRole}/main">Вернуться на главную</a>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>