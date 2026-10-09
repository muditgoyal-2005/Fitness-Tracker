<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="header.jspf" %>
<section class="page-heading"><span class="eyebrow">REQUEST INTERRUPTED</span><h1>That route is <em>off track.</em></h1><p>The page may have moved or the request could not be completed.</p><a class="button button-lime" href="${pageContext.request.contextPath}/dashboard">Return to dashboard</a></section>
<%@ include file="footer.jspf" %>