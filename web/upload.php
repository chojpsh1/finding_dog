<html>
<head>
	<meta charset = "UTF-8">
	<title>upload image</title>
	<link rel="stylesheet" type="text/css" href="./css/index.css?ver=30">
	<script type="text/javascript" src="./js/index.js?ver=301"></script>
</head>
<body>
	<div id="div0">
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
    // 설정
    $uploads_dir = './uploads';
    $allowed_ext = array('jpg','jpeg','png','gif');

    // 변수 정리
    $error = $_FILES['myfile']['error'];
    $name = $_FILES['myfile']['name'];
    $ext = array_pop(explode('.', $name));
    // 오류 확인
    if( $error != UPLOAD_ERR_OK ) {
    	switch( $error ) {
    		case UPLOAD_ERR_INI_SIZE:
    		case UPLOAD_ERR_FORM_SIZE:
    			echo "파일이 너무 큽니다. ($error)";
    			break;
    		case UPLOAD_ERR_NO_FILE:
    			echo "<h3>파일이 첨부되지 않았습니다. ($error)</h3>";
    			break;
    		default:
    			echo "파일이 제대로 업로드되지 않았습니다. ($error)";
    	}
    	exit;
    }

    // 확장자 확인
    if( !in_array($ext, $allowed_ext) ) {
    	echo "허용되지 않는 확장자입니다.";
    	exit;
    }

    // 파일 이동
    move_uploaded_file( $_FILES['myfile']['tmp_name'], "$uploads_dir/$name");

    // 파일 정보 출력
     
     echo "<h2>파일 정보</h2>
     		<li>파일명: $name</li>
     		<li>확장자: $ext</li>
    		<li>파일형식: {$_FILES['myfile']['type']}</li>
     		<li>파일크기: {$_FILES['myfile']['size']} 바이트</li>";
       
    // 네이버 얼굴인식 Open API 예제
    $client_id = "np3sEe4KOnVbGJrm5HgP";
    $client_secret = "aiY473AvTr";
    $url = "https://openapi.naver.com/v1/vision/face"; // 얼굴감지
    $is_post = true;
    $ch = curl_init();
    $filename = $_FILES['myfile']['tmp_name'];
    $filesize = filesize($filename);
    if($filesize > 2*1024*1024) {
        echo "2MB 이하의 이미지를 올려주세요.";
        exit;
    }

    $file_name = $_FILES['myfile']['tmp_name']; // 업로드할 파일명
    $cfile = curl_file_create($file_name,'image/jpeg','test_name');
    $postvars = array("filename" => $filename, "image" => $cfile);
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, $is_post);
    curl_setopt($ch, CURLOPT_INFILESIZE, $filesize);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $postvars);
    curl_setopt($ch, CURLINFO_HEADER_OUT, true);
    $headers = array();
    $headers[] = "X-Naver-Client-Id: ".$client_id;
    $headers[] = "X-Naver-Client-Secret: ".$client_secret;
    $headers[] = "Content-Type:multipart/form-data";
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    $response = curl_exec ($ch);
    $status_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    // 헤더 내용 출력
    $headerSent = curl_getinfo($ch, CURLINFO_HEADER_OUT );
    // echo $headerSent;
    // echo "<br>[status_code]:".$status_code."<br>";
    curl_close ($ch);
    if($status_code == 200) {
      $response = strstr($response, "roi");
      $response = str_replace(',', ':', $response);
      $string = explode(':', $response);
	
	echo "<h4><br/>사진업로드가 정상적으로 되었습니다!</h4>";
    } else {
       echo "Error 내용:".$response;
    }
    
    ?>

   <form action="insert.php" method ="get">
	<input type="hidden" name="x" value="<?php echo $string[2]; ?>">
	<input type="hidden" name="y" value="<?php echo $string[4]; ?>">
	<input type="hidden" name="gender" value=<?php echo $string[13]; ?>>
	<input type="hidden" name="age" value=<?php echo $string[18]; ?>>
  	<br/>
	<div>핸드폰 번호: <input type="text" name="phone"></div>
	<br/>
	<div>주인 이름: <input type="text" name="name"></div>
	<br/>
	<input type = "submit" value = "전달">
   </form>
  </main>
 </body>
</html>

