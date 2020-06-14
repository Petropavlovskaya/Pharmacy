<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
<ul id="menu-v">
    <li><a href="${pageContext.request.contextPath}/admin/main"><br>Главнвя<br><br></a></li>

    <li><span><br>Аккаунты пользователей<br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/account/create"><br>Создать новый<br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/admin/account/list"><br>Просмотр и управление<br><br></a>
            </li>
        </ul>
    </li>

    <li><span><br>Личный кабинет<br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/cabinet/profile"><br>Профиль<br><br></a></li>
        </ul>
    </li>

    <li><a href="${pageContext.request.contextPath}/logout"><br>Выход (${login})<br><br></a></li>


</ul>
</html>