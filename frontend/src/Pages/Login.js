import React, { useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
//아이디,비번 입력창
import TextField from "@material-ui/core/TextField";
//로그인 버튼
import Button from "@material-ui/core/Button";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(10, 75),
  },
  textField: {
    "& > *": {
      margin: theme.spacing(1),
      width: 300,
    },
  },
  loginButton: {
    "& > *": {
      margin: theme.spacing(3, 1, 2, 1),
      width: 300,
      height: 40,
    },
  },
  signup: {
    "& > *": {
      margin: theme.spacing(0, 0, 0, 16),
    },
  },
}));

export default function Login() {
  const [account, setAccount] = useState("");
  const [password, setPassword] = useState("");

  const classes = useStyles();

  const handleClick = () => {
    if (account === "" || password === "") {
      alert("아이디와 비밀번호를 모두 입력하세요");
    } else {
      var myHeaders = new Headers();
      myHeaders.append("Content-Type", "application/json");

      var raw = JSON.stringify({ account: account, password: password });

      var requestOptions = {
        method: "POST",
        headers: myHeaders,
        body: raw,
        redirect: "follow",
      };

      fetch("/api/users/token", requestOptions)
        .then((response) => {
          console.log(response);
          response.json().then((result) => {
            console.log(result);
            if (result.error_code !== null) {
              if (result.error_code === 42) {
                // 로그인 필요한 경우
                if (!localStorage.getItem("authorization")) {
                  alert(
                    "로그인이 필요한 기능입니다, 로그인 페이지로 이동합니다."
                  );
                  window.location.href = "/login";
                } else {
                  alert("잘못된 접근입니다.");
                }
                return;
              }

              alert(result.message);
              return;
            }

            localStorage.setItem("authorization", response.headers.get("Authorization"));
            var temp = localStorage.getItem("authorization");
            var jwt_decode = require("jwt-decode");
            var decodeToken = jwt_decode(temp.replace("bearer ", ""));
            console.log("decodeToken : " + decodeToken);
            localStorage.setItem("user_idx", decodeToken.userIdx);
            localStorage.setItem("account", result.data.account);
            localStorage.setItem("name", result.data.name);
            localStorage.setItem("birth", result.data.birth);
            localStorage.setItem("gender", result.data.gender);
            localStorage.setItem("email", result.data.email);
            localStorage.setItem("refresh_token", result.data.token);

            window.history.back();
          });
        })
        .catch((error) => console.log("error", error));
    }
  };

  return (
    <div className={classes.root}>
      <h1
        style={{
          color: "#ff7043",
          fontSize: "64px",
        }}
      >
        &emsp;WWW
      </h1>

      <form className={classes.textField} noValidate autoComplete="off">
        <TextField
          id="account"
          label="아이디를 입력해주세요"
          variant="outlined"
          value={account}
          onChange={({ target: { value } }) => setAccount(value)}
        />
        <TextField
          id="password"
          label="비밀번호를 입력해주세요"
          type="password"
          autoComplete="current-password"
          value={password}
          onChange={({ target: { value } }) => setPassword(value)}
          variant="outlined"
        />
      </form>

      <div className={classes.loginButton}>
        <Button variant="contained" color="primary" onClick={handleClick}>
          <span style={{ color: "#fafafa", fontWeight: "bold" }}>로그인</span>
        </Button>
      </div>

      <div className={classes.signup}>
        <a href="/login/signup">회원가입</a>
      </div>
    </div>
  );
}
