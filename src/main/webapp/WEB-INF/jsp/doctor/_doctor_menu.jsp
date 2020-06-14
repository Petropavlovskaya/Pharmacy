<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
<ul id="menu-v">
    <li><a href="${pageContext.request.contextPath}/doctor/main"><br>Главнвя<br><br></a></li>

    <li><span><br>Рецепты<br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/doctor/recipe/create"><br>Выписать рецепт<br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/doctor/recipe/ordered"><br>Заказы на рецепт<br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/doctor/recipe/history"><br>История рецептов<br><br></a></li>
        </ul>
    </li>

    <li><span><br>Личный кабинет<br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/doctor/cabinet/profile"><br>Профиль<br><br></a></li>
        </ul>
    </li>

    <li><a href="${pageContext.request.contextPath}/logout"><br>Выход (${accountLogin})<br><br></a></li>

</ul>
</html>
