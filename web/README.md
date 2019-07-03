# Finding dog's web project
Web page for finding stary dog via NAVER Clova Face Recognition API

We consider increasing number of stary dogs evey year, massive data, and complicated pet dog registration procedure.
via face recognition AI, the problems can be solved a little. So, we use NAVER Clova Face Recognition API.

## Local Env (Ubuntu 16.04)

##### APM (Apache + Php + MySQL DB)

## Setup

##### Install the Apache2 web server

> sudo apt install apache2

##### Install the MySQL server

> sudo apt install mysql-server

##### Install PHP

<pre>sudo apt install php-mysql
sudo apt install libapache2-mod-php7.0
sudo a2enmod php7.0
sudo systemctl restart apache2.service </pre>

##### Create Naver Clova Face Recognition API key

> Follow the Naver Clova API documentations. (https://developers.naver.com/docs/clova/api/CFR/API_Guide.md#UsingAPI)

## code
* index.html
>index page

* upload.php

>upload pet dog's picture which owner want to register on web site.

>uploaded picture be delivered as variable of API.

* connection.php
>connect to mysql DB.

* detect.php
>Deliever information of uploaded stary dog's picture via API to search.php

* search.php
>Check if there is information matching with delievered information by detect.php in DB.

> If there is, show the profile of the stary dog's owner.
