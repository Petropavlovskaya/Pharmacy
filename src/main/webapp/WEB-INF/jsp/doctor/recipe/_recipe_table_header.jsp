<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<thead>
<th><fmt:message key="label.tableHeader.patient"/></th>
<th><fmt:message key="label.tableHeader.medicineName"/></th>
<th><fmt:message key="label.tableHeader.dosage"/></th>
<th><fmt:message key="label.tableHeader.exp"/></th>
<th colspan="2"><fmt:message key="label.tableHeader.action"/></th>
</thead>
