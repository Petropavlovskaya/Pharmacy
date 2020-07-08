<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<thead>
<th><fmt:message key="label.tableHeader.patient"/></th>
<th><fmt:message key="label.tableHeader.medicineName"/> (<fmt:message key="label.tableHeader.dosageInRecipe"/>)</th>
<th><fmt:message key="label.tableHeader.recipeValid"/></th>
<th><fmt:message key="label.tableHeader.action"/></th>
</thead>
