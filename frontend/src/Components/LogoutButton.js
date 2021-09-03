import React from "react";
import { withRouter } from "react-router-dom";

import Button from "@material-ui/core/Button";

function LogoutButton() {
  const handleClick = () => {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", `Bearer ${localStorage.getItem("authorization")}`);

    var raw = JSON.stringify({
      refresh_token: localStorage.getItem("refresh_token"),
    });

    var requestOptions = {
      method: "DELETE",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    var userIdx = localStorage.getItem("user_idx");
    console.log(userIdx);
    fetch("/api/auth", requestOptions)
      .then((response) => response.text())
      .then((result) => {
        console.log(result);
        localStorage.clear();
        window.location.reload();
      })
      .catch((error) => console.log("error", error));
  };

  return (
    <div>
      <Button variant="contained" color="primary" onClick={handleClick}>
        <span style={{ color: "#fafafa", fontWeight: "bold" }}>로그아웃</span>
      </Button>
    </div>
  );
}

export default withRouter(LogoutButton);
