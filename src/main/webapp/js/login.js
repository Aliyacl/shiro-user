$(function () {
    console.log($('#submit'))
    $('#submit').click(function (){
        var username= $('#username').val();
        var password = $('#password').val();
        $.ajax({
            url:"http://localhost:8080/login/login.do",
            type:"POST",
            async:false,
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            data: JSON.stringify({ "username": username, "password": password }),
            success: function(data) {
            	alert("登录成功")
            }

        })
    })
});