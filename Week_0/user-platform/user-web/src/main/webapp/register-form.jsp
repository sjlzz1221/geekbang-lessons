<head>
<jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
	<title>My Home Page</title>
    <style>
      .bd-placeholder-img {
        font-size: 1.125rem;
        text-anchor: middle;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
      }

      @media (min-width: 768px) {
        .bd-placeholder-img-lg {
          font-size: 3.5rem;
        }
      }
    </style>
</head>
<body>
	<div class="container">
		<form action="/register" method="post" class="form-signin">
			<h1 class="h3 mb-3 font-weight-normal">注册</h1>
			<label for="inputName" class="sr-only">用户名</label>
			<input
					type="text" name="inputName" id="inputName" class="form-control"
					placeholder="请输入用户名" required autofocus>

			<label for="inputPassword" class="sr-only">密码</label>
			<input
					type="password" name="inputPassword" id="inputPassword" class="form-control"
					placeholder="请输入密码" required>

			<label for="inputEmail" class="sr-only">电子邮件</label>
			<input
				type="email" name="inputEmail" id="inputEmail" class="form-control"
				placeholder="请输入电子邮件" required autofocus>

			<label for="inputPhone" class="sr-only">手机号码</label>
			<input
					type="text" name="inputPhone" id="inputPhone" class="form-control"
					placeholder="请输入手机号码" required autofocus>

			<button class="btn btn-lg btn-primary btn-block" type="submit">注册</button>
			<p class="mt-5 mb-3 text-muted">&copy; 2017-2021</p>
		</form>
	</div>
</body>
