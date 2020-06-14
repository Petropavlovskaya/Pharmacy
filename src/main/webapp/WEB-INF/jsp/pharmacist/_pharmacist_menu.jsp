<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
<ul id="menu-v">
    <li><a href="${pageContext.request.contextPath}/pharmacist/main"><br>Главнвя<br><br></a></li>

    <li><span><br>Лекарственные средства<br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/pharmacist/medicine/list"><br>Список лекарственных
                средств<br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/pharmacist/medicine/create"><br>Добавить лекарственное
                средство<br><br></a></li>
        </ul>
    </li>

    <li><span><br>Личный кабинет<br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/pharmacist/cabinet/profile"><br>Профиль<br><br></a></li>
        </ul>
    </li>

    <li><a href="${pageContext.request.contextPath}/logout"><br>Выход (${accountLogin})<br><br></a></li>

</ul>
</html>
