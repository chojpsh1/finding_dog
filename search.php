<html>
<head>
	<meta charset = "UTF-8">
	<title>search image</title>
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

		$host = "127.0.0.1";
		$user = "user";
		$pw = "passwd";
		$db_name = "finding_dog";

		$conn = mysqli_connect($host, $user, $pw, $db_name);
	
		if( mysqli_connect_errno($conn)){
			die("Connection failed: " . mysqli_connect_errno());
		}
	
		$find_sql = "SELECT dog_index FROM dog_list where dog_x between '$send_x'-15 and '$send_x'+15 or dog_y between '$send_y'-15 and '$send_y'+15";

		$result = mysqli_query($conn, $find_sql);
	
		if( !result ){
			die('Error : ' . mysqli_errno() );
		}
		
		while($row = mysqli_fetch_array($result)){
			$phone_sql = "SELECT host_phone FROM host_list where host_index='$row[0]'";
			$name_sql = "SELECT host_name FROM host_list where host_index='$row[0]'";
			$finding_p = mysqli_query($conn, $phone_sql);
			$finding_n = mysqli_query($conn, $name_sql);
			if( !$finding_p || !$finding_n ){
				die('Error : ' . mysqli_errno() );
			}
			$finding_Plist = mysqli_fetch_array($finding_p);
			$finding_Nlist = mysqli_fetch_array($finding_n);

			echo "<h2>해당 유기견의 주인 전화번호</h2>";
	 		echo $finding_Plist[0];
			echo "<br />\n";
			echo "<h2>해당 유기견의 주인 이름</h2>";
			echo $finding_Nlist[0];
			echo "<br />\n";
		}

		mysqli_close($conn);
?>
</main>
</body>
</html>
