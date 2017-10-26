<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>JarManager</title>


<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<style>
.modal-body div {
	padding: 10px;
}

.form-table input {
	width: 400px;
}

.form-control {
	margin: 10px;
}
</style>


</head>

<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="/JarManager/index">JarManager</a>
		</div>
		<ul class="nav navbar-nav">
			<li><a data-toggle="tab" href="#home">Start</a></li>
			<li><a data-toggle="tab" href="/JarManager/JarPeojectVOs">Page 1</a></li>
			<li><a data-toggle="tab" href="#menu2">設定</a></li>
		</ul>
	</div>

</nav>
<h2>管理程序</h2>
<div class="container">
	<div class="container" id="jar_table">
		<p>目前所管理的程序:</p>
		<div>
			<button type="button" class="btn btn-xs btn-success" data-toggle="modal" data-target="#insertModal">
				<i class="fa fa-check"></i> 新增
			</button>
		</div>
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
								<c:when test="${!isRun}">
									<span class="red">已關閉</span>
								</c:when>
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
								<c:otherwise>
									<span class="red">準備重啟</span>
								</c:otherwise>
							</c:choose></td>
						<td>${(listValue.timeSeries)/1000}秒</td>
						<td>${listValue.description}</td>
						<td><a class="btn btn-xs btn-info" href="#" role="button"> <i class="fa fa-pencil"></i>
						</a> <a class="btn btn-xs btn-danger" href="#" role="button"> <i class="fa fa-trash-o"></i>
						</a></td>
				</c:forEach>
		</table>
	</div>
	<hr>
	<footer>
		<p>JarManager 2017</p>
	</footer>
</div>



<div id="dialog-confirm" title="刪除資料" style="display: none;">
	<p>是否確認刪除該筆資料</p>
</div>

<div id="message" align="center">
	<div id="text"></div>
</div>

<!-- Modal -->
<div class="modal fade" id="insertModal" role="dialog">
	<div class="modal-dialog">

		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">新增管理程序</h4>
			</div>
			<div class="modal-body">

				<div>
					<span>檔案名稱：</span> <span><input type="text" id="dialog_fileName" name="dialog_fileName" class="form-control" /></span>
				</div>
				<div>
					<span>檔案編號：</span> <span><input type="text" id="dialog_beatID" name="dialog_beatID" class="form-control" /></span>
				</div>
				<div>
					<span>檔案路徑：</span> <span><input type="text" id="dialog_jarFilePath" name="dialog_jarFilePath" class="form-control" /></span>
				</div>
				<div>
					<span>檔案說明：</span> <span><input type="text" id="dialog_description" name="dialog_description" class="form-control" /></span>
				</div>
				<div>
					<span>間隔發送：</span> <span><input type="text" id="dialog_timeSeries" name="dialog_description" class="form-control" /></span>
				</div>
				<div>

					<span>檔案xml路徑：</span>
					<button type="button" class="btn btn-xs" id="add_xml">
						<i class="fa fa-check"></i> 新增
					</button>
					<span><input type="text" id="dialog_filePathXMLList" name="dialog_filePathXML" class="form-control" /></span> <span id="xml"></span>
				</div>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal" id="insert_jar">確定</button>
				<button type="button" class="btn btn-default btn_close" data-dismiss="modal">Close</button>
			</div>
		</div>

	</div>
</div>


<script type="text/javascript">
	$(function() {

		$("#insertModal .btn_close").click(function() {
			initInsert();
		});

		$("#add_xml")
				.click(
						function() {

							$("#xml")
									.append(
											'<input type="text"  name="dialog_filePathXML" class="form-control" />');
						});
		$("#insert_jar").click(function() {

			insertJar();
		});

	});

	function initInsert() {
		var all_Inputs = $("#insertModal input[type=text]");
		all_Inputs.val("");
		$("#xml").empty();
	}

	function insertJar() {
		var dialog_fileName = $("input[name='dialog_fileName']").val();
		var dialog_beatID = $("input[name='dialog_beatID']").val();
		var dialog_jarFilePath = $("input[name='dialog_jarFilePath']").val();
		var dialog_description = $("input[name='dialog_description']").val();
		var dialog_timeSeries = $("input[name='dialog_timeSeries']").val();
		var dialog_filePathXMLList = new Array();

		$("input[name='dialog_filePathXML']").each(function() {
			dialog_filePathXMLList.push($(this).val());
		});

		var data = {}
		data["fileName"] = dialog_fileName;
		data["beatID"] = dialog_beatID;
		data["jarFilePath"] = dialog_jarFilePath;
		data["description"] = dialog_description;
		data["filePathXMLList"] = dialog_filePathXMLList;
		data["dialog_timeSeries"] = dialog_timeSeries*1000;
		console.log(JSON.stringify(data));
		$.ajax({
			type : "POST",
			url : "/JarManager/JarProjectVO",
			contentType : "application/json",
			success : function(msg) {
				if (msg) {

					location.reload(true);
				} else {
					alert("Cannot add to list !");
				}
			},

			data : JSON.stringify(data)
		});
	}
</script>


</body>
</html>