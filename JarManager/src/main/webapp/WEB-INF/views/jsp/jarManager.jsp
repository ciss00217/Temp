<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>JarManager</title>

<spring:url value="/resources/core/css/jarmassage.css" var="jarmassageCss" />
<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
<link href="${jarmassageCss}" rel="stylesheet" />

</head>

<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="/JarManager/index">JarManager</a>
		</div>
         <ul class="nav navbar-nav">
                <li><a data-toggle="tab" href="#home">Start</a></li>
                <li><a  href="/JarManager/JarProjectVOs">Page 1</a></li>
                <li><a data-toggle="tab" href="#menu2">設定</a></li>
            </ul>
      </div>
      
</nav>

<div class="jumbotron">
	<div class="container">
		<h1>管理程序系統</h1>
		<p>開始</p>
		<p>
			<a class="btn btn-primary btn-lg" href="/JarManager/Delete/queue" role="button">Start</a>
		</p>
	</div>
</div>

<div class="container">

	<div class="container">
		<h2>管理程序</h2>
		<p>目前所管理的程序:</p>


		<table class="table table-striped">
			<thead>
				<tr>
					<th>程序名稱</th>
					<th>編號</th>
					<th>狀態</th>
					<th>間隔發送</th>
					<th>說明</th>
					<th>控制</th>
				</tr>
			</thead>

			<tbody>
				<c:forEach var="listValue" items="${jarProjectVOList}">
					<tr>
						<td>${listValue.fileName}</td>
						<td>${listValue.beatID}</td>
						<td>
						${listValue.notFindCount}
						<c:choose>
								<c:when test="${listValue.notFindCount eq 0}">
									<span class="blue">執行中</span>
								</c:when>
								<c:when test="${listValue.notFindCount eq 1}">
									<span class="blue">執行中</span>
								</c:when>
								<c:when test="${listValue.notFindCount eq 2}">
									<span class="blue">執行中</span>
								</c:when>
								<c:when test="${listValue.notFindCount eq 3}">
									<span class="blue">執行中</span>
								</c:when>
								<c:when test="${listValue.notFindCount eq 4}">
									<span class="red">異常</span>
								</c:when>
								<c:otherwise >
									<span class="red">準備重啟</span>
								</c:otherwise>
							</c:choose></td>
						<td>${(listValue.timeSeries)/1000}秒</td>
						<td>${listValue.description}</td>
						<td><a class="btn btn-default" href="#" role="button">View details</a></td>
				</c:forEach>
		</table>
	</div>


	<hr>
	<footer>
		<p>JarManager 2017</p>
	</footer>
</div>

<spring:url value="/resources/core/js/hello.js" var="coreJs" />
<spring:url value="/resources/core/js/tether.min.js" var="tether" />
<spring:url value="/resources/core/js/bootstrap.min.js" var="bootstrapJs" />
<spring:url value="/resources/core/js/jquery-3.2.1.slim.min.js" var="jquery" />

<script src="${jquery}"></script>
<script src="${coreJs}"></script>
<script src="${tether}"></script>
<script src="${bootstrapJs}"></script>



</body>
</html>