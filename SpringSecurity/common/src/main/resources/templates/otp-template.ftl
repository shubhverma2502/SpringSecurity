<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title style="color: #029a9c;">OTP</title>
    <style>
        body {
font-family: 'Source Sans Pro', sans-serif;
line-height: 1.6;
background-color: #f4f4f4;
margin: 0;
padding: 20px;
}

.container {
background-color: #ffffff;
padding: 20px;
border-radius: 10px;
box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
max-width: 600px;
margin: 0 auto;
}

h2 {
color: #029a9c;
margin-bottom: 20px;
text-align: center;
font-size: 28px;
}

p {
color: #555555;
font-size: 16px;
margin-bottom: 15px;
line-height: 1.6;
}

strong {
color: #029a9c;
}

</style>
<link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,700&display=swap" rel="stylesheet" type="text/css">
</head>
<body>

 <div class="container">
        <div style="text-align: center; color: #029a9c;">

            <img style="width: 190px;" src="http://ec2-54-187-227-252.us-west-2.compute.amazonaws.com:3000/logo.png"/>

            <h2>New OTP</h2>
        </div>
    <p style="font-size:1.1em">Hello <b>${name}</b>,</p>
    <p>Thank you for choosing Security. Use the following OTP to complete the authentication.</p>
    <h2 style="background: #029a9c;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;">${otp}</h2>
    </div>
</div>
</body>
