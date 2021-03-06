<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>On-line pharmacy. Main</title>
    <style>
      <%@include file="/css/style.css" %>
    </style>
    <link href="${pageContext.request.contextPath}/images/pharmacy_small.gif" rel="icon" type="image/gif"/>
  </head>
  <body>

  <div id="logo">
    <%@include file="../_header.jsp" %>
  </div>
  <div id="line"></div>
  <div id="menu">
    <c:import url="_pharmacist_menu.jsp"/>
  </div>
  <div id="center">
    <c:import url="../_center.jsp"/>
  </div>
  <div id="right">
    <c:import url="../_right.jsp"/>
  </div>
  <div id="footer">
    <c:import url="../_footer.jsp"/>
  </div>

  </body>
</html>
