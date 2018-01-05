<?php
	$host = "127.0.0.1";
	$user = "chojpsh1";
	$pw = "gkwjd7911";
	$db_name = "finding_dog";
	
	$conn = mysqli_connect($host, $user, $pw, $db_name);
	
	if( mysqli_connect_errno($conn)){
		die("Connection failed: " . mysqli_connect_errno());
	}
	else{
		echo "hi";
	}
	/*
	$db_connect = mysql_select_db($db_name, $connect);
	
	if( $db_connect )
		echo "connection success!"
	}
	else{
		echo "connection fail!"
	}*/
?>