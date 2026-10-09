<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:choose>
  <c:when test="${not empty sessionScope.user and sessionScope.user.role == 'ADMIN'}"><c:redirect url="/admin/dashboard" /></c:when>
  <c:when test="${not empty sessionScope.user}"><c:redirect url="/dashboard" /></c:when>
  <c:otherwise><c:redirect url="/auth/login" /></c:otherwise>
</c:choose>