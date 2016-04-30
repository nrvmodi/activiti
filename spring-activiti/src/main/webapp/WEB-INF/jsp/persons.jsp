<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head></head>

<body>
	Persons :
	<br>
	<table>
		<tr>
			<th>Person Id</th>
			<th>Person Name</th>
		</tr>

		<c:forEach items="${persons}" var="person">
			<tr>
				<td>${person.id}</td>
				<td>${person.firstName}${person.lastName}</td>
			</tr>
		</c:forEach>
	</table>
</body>

</html>