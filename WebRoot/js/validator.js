window.$ && $(function(){

//验证初始化
$('#signup_form').validator({ 
    theme: 'simple_right',
    stopOnError:true,
    timely: 2,
    //自定义规则（PS：建议尽量在全局配置中定义规则，统一管理）
    rules: {
        username: [/^[_a-zA-Z0-9]+$/, '用户名无效! 仅支持字母与数字。'],
         qq: function(element, param, field) {
                    var qq = $(this).val();
                    if (qq == 0)
                        return true;
                    var patrn = /^[1-9]\d{4,10}$/;
                    if (patrn.test(qq)) {
                        qq = parseInt(qq, 10);
                        if (qq > 10000 && qq < 30000000000) {
                            return true;
                        }
                    }
                    return ("请输入正确的QQ号码!或者不填写。");
                }
    },
    fields: {
        "user_id": {
            rule: "required",
            tip: "输入你的名字与姓氏。",
            ok: "名字很棒。",
            msg: {required: "全名必填!"}
        },
        "email": {
            rule: "email;remote[check/email.php]",
            tip: "你的邮件地址是什么?",
            ok: "我们将会给你发送确认邮件。",
            msg: {
                required: "电子邮箱地址必填!",
                email: "不像是有效的电子邮箱。"
            }
        },
        "password": {
            rule: "required;length[6~];password;strength",
            tip: "6个或更多字符! 要复杂些。",
            ok: "",
            msg: {
                required: "密码不能为空!",
                length: "密码最少为6位。"
            }
        },
        "rptPassword": {
            rule: "required;match(password)",
            tip: "6个或更多字符! 要复杂些。",
            ok: "",
            msg: {
                required: "密码不能为空!"
            }
        },
        "qq": {
            rule: "qq",
            tip: "别担心,你可以稍后进行修改。",
            ok: "用户名可以使用。<br>你可以稍后进行修改。",
            msg: {required: "用户名必填!<br>你可以稍后进行修改。"}
        }
    },
    //验证成功
    valid: function(form) {
        $.ajax({
            url: 'register',
            type: 'POST',
            data: $(form).serialize(),
            success: function(d){
                $('#result').fadeIn(300).delay(2000).fadeOut(500);
            }
        });
    },
    //验证失败
    invalid: function(form) {
        //按钮动画效果
        $('#btn-submit').stop().delay(100)
            .animate({left:-5}, 100)
            .animate({left:5}, 100)
            .animate({left:-4}, 100)
            .animate({left:4}, 100)
            .animate({left:-3}, 100)
            .animate({left:0}, 100);
    }
});

});