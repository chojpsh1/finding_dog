# finding_dog
Web page for finding stary dog via NAVER Clova Face Recognition API

We consider increasing number of stary dogs evey year, massive data, and complicated pet dog registration procedure.
via face recognition AI, the problems can be solved a little. So, we use NAVER Clova Face Recognition API.

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
