<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
<ul id="menu-v">
    <li><a href="${pageContext.request.contextPath}/main"><br>Главная<br><br></a></li>
    <li><a href="${pageContext.request.contextPath}/medicine"><br>Список лекарственных средств<br><br></a></li>

    <c:choose>
        <c:when test="${empty accountLogin}">
            <li><a href="${pageContext.request.contextPath}/signup"><br>Регистрация<br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/login"><br>Вход<br><br></a></li>
        </c:when>
        <c:otherwise>
            <li><a href="${pageContext.request.contextPath}/logout"><br>Выход<br><br></a></li>
        </c:otherwise>
    </c:choose>


</ul>
</html>
