<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>On-line pharmacy. Create new recipe</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="${pageContext.request.contextPath}/images/Pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>


<div id="logo">
    <c:import url="../../_header.jsp"/>
</div>
<div id="line"></div>
<div id="menu">
    <c:import url="../_doctor_menu.jsp"/>
</div>


<div id="center_no_right">
    <form action="${pageContext.request.contextPath}/doctor/recipe/create" method="post">
        <p><input list="cocktail" name="list"></p>
        <datalist id="cocktail">
            <option>Аперитивы</option>
            <option>Горячие</option>
            <option>Десертные</option>
            <option>Диджестивы</option>
            <option>Молочные</option>
            <option>Слоистые</option>
        </datalist>
        <br>
        <br>
        <input type="submit" value=" Appoint ">
        <input type="hidden" name="customerCommand" value="appointRecipe">


    </form>
</div>


<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>


</body>
</html>
