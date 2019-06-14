$(function () {
    console.log($('#submit'))
    $('#submit').click(function (){
        var username= $('#username').val();
        var password = $('#password').val();
        var email= $('#email').val();
        $.ajax({
            url:"http://localhost:8080/login/register.do",
            type:"POST",
            async:false,
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            data: JSON.stringify({ "username": username, "password": password ,"email":email}),
            success: function(data) {
            	alert("注册成功")
            }

        })
    })
});