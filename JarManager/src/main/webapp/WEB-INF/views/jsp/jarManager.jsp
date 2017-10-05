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
	</div>
</nav>

<div class="jumbotron">
	<div class="container">
		<h1>管理程序系統</h1>
		<p>開關說明</p>
		<p>
			<a class="btn btn-primary btn-lg" href="/JarManager/Delete/queue" role="button">Clear Queue</a>
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
						<td><c:choose>
								<c:when test="${listValue.isAlive}">
									<span class="blue">執行中</span>
								</c:when>
								<c:otherwise>
									<span class="red">無反應</span>
								</c:otherwise>
							</c:choose></td>
						<td><c:choose>
								<c:when test="${listValue.isAlive}">
								${(listValue.timeSeries)/1000} 秒
							</c:when>
								<c:otherwise>
								--
							</c:otherwise>
							</c:choose></td>
						<td>${listValue.description}</td>
						<td><c:choose>
								<c:when test="${listValue.isAlive}">
									<a class="btn btn-default" href="#" role="button">View details</a>
								</c:when>
								<c:otherwise>
								--
								</c:otherwise>
							</c:choose></td>
				</c:forEach>
		</table>
	</div>
<!-- 
	<c:if test="${not empty jarProjectVOList}">
		<div class="row">
			<c:forEach var="listValue" items="${jarProjectVOList}">
				<div class="col-md-4">
					<h2>${listValue.fileName}</h2>
					<p>ID: ${listValue.beatID}</p>

					<c:choose>
						<c:when test="${listValue.isAlive}">
							<p>
								狀態: <span class="blue">執行中</span>
							</p>
							<p>間隔發送時間: ${(listValue.timeSeries)/1000} 秒</p>

						</c:when>

						<c:otherwise>
							<p>
								狀態: <span class="red">無反應</span>
							</p>

						</c:otherwise>
					</c:choose>

					<p>
						<a class="btn btn-default" href="#" role="button">View details</a>
					</p>
				</div>

			</c:forEach>
		</div>

	</c:if>
 -->

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