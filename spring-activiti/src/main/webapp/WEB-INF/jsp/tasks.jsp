<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head></head>

<body>
	Tasks :
	<br>
	<table>
		<tr>
			<th>Task Id</th>
			<th>Task Name</th>
			<th>Task Assignee</th>
			<th>Task's Process Instance</th>
			<th>Is Completed?</th>
		</tr>

		<c:forEach items="${tasks}" var="task">
			<tr>
				<td>${task.id}</td>
				<td>${task.name}</td>
				<td>${task.assignee}</td>
				<td><a href="/diagram/${task.processInstanceId}">Diagram</a></td>
				<td><a href="/tasks/complete/${task.id}">Complete</a></td>
			</tr>
		</c:forEach>
	</table>
</body>

</html>