<html>
<head>
	<meta charset = "UTF-8">
	<title>upload image</title>
	<link rel="stylesheet" type="text/css" href="./css/index.css?ver=20">
	<script type="text/javascript" src="./js/index.js?ver=201"></script>
</head>
<body>
	<div id="div0" onclick="move(0)" >
		<a href="./index.html"><img calss="main_dog" src="./img/main_dog2.png" style="width:8%"></a>
		<a href="./index.html"><img calss="main_dog" src="./img/main_dog.png" style="width:15%"></a>
	</div>
	<header >
			<!-- Header (Menu bar) -->
			<div  id="menu_div" class="Bar" style="cursor:pointer">
				<a href="./index.html"><button class="Menu">홈</button></a>
				<button class="Menu"onclick="move(1)">사진보기</button>
				<a href="./index.html"><button class="Menu" onclick="move(2)">반려견등록</button></a>
				<button class="Menu" onclick="move(3)">유기견찾기</button>
			</div>
	</header>
	<main id="content">
<?php
	$send_x = $_GET['x'];
	$send_y = $_GET['y'];
	$send_gender = $_GET['gender'];
	$send_age = $_GET['age'];
	
	$send_phone = $_GET['phone'];
	$send_name = $_GET['name'];
	$host = "127.0.0.1";
	$user = "user";
	$pw = "passwd";
	$db_name = "finding_dog";
	$conn = mysqli_connect($host, $user, $pw, $db_name);
	if( mysqli_connect_errno($conn)){
		die("Connection failed: " . mysqli_connect_errno());
	}
	$dog_sql = "INSERT INTO dog_list (dog_x, dog_y, dog_gender, dog_age) VALUES ('$send_x', '$send_y', '$send_gender', '$send_age')";
	$host_sql = "INSERT INTO host_list (host_phone, host_name) VALUES ('$send_phone', '$send_name')";
	if( (!mysqli_query($conn, $dog_sql)) || (!mysqli_query($conn, $host_sql)) ){
		die('Error : ' . mysqli_errno() );
	}
	echo "<h3>반려견의 사진과 주인의 정보가 정상적으로 저장되었습니다!</h3>";

	mysqli_close($conn);
?>
</main>
</body>
</html>
