// 인증이 필요한 컴포넌트를 위한 전용 라우트
import React from "react";
import { Route, Redirect } from "react-router-dom";

export function ReToken() {
  var myHeaders = new Headers();

  myHeaders.append("Content-Type", "application/json");
  myHeaders.append("Authorization", localStorage.getItem("authorization"));

  var raw = JSON.stringify({
    refresh_token: localStorage.getItem("refresh_token"),
  });

  var requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
    redirect: "follow",
  };

  var userIdx = localStorage.getItem("user_idx");

  fetch("/api/users/" + userIdx + "/token", requestOptions)
    .then((response) => {
      localStorage.setItem("authorization", response.headers.get("Authorization"));
      response.json().then((result) => {
        console.log(result);
        if (result.error_code != null) {
          alert("재로그인이 필요합니다.");
          window.location.href = "/login";
        }
      });
    })
    .catch((error) => console.log("error", error));
}

function AuthRoute({ component: Component, render, ...rest }) {
  if (localStorage.getItem("authorization")) {
    var temp = localStorage.getItem("authorization");
    var jwt_decode = require("jwt-decode");
    var decodeToken;
    if (temp == null) {
      decodeToken = jwt_decode(temp.replace("bearer ", ""));
      // token 유효시간 체크
      if (decodeToken.exp * 1000 - Date.now() < 1000 * 30) {
        ReToken();
      }
    }
  }

  return (
    <Route
      {...rest}
      render={(props) =>
        localStorage.getItem("authorization") ? (
          render ? (
            render(props)
          ) : (
            <Component {...props} />
          )
        ) : (
          <Redirect
            to={{ pathname: "/login", state: { from: props.location } }}
          />
        )
      }
    />
  );
}

export default AuthRoute;
