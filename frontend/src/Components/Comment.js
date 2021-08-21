import React, { Component } from "react";
import { Button } from "@material-ui/core";
// 토큰 재발급
var ReToken = require("../AuthRoute");

class Comment extends Component {
  state = { goodNum: this.props.goodNum, badNum: this.props.badNum };

  handleGood = ({ target: { goodNum } }) => {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", localStorage.getItem("authorization"));

    var raw = "";

    var requestOptions = {
      method: "POST",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    fetch("/api/comments/" + this.props.cmtIdx + "/like", requestOptions)
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        if (result.error_code !== null) {
          if (result.error_code === 44) {
            ReToken.ReToken();
            return;
          }

          if (result.error_code === 42) {
            // 로그인 필요한 경우
            if (!localStorage.getItem("authorization")) {
              alert("로그인이 필요한 기능입니다, 로그인 페이지로 이동합니다.");
              window.location.href = "/login";
            } else {
              alert("잘못된 접근입니다.");
            }
            return;
          }

          alert(result.message);
        }

        this.setState({ goodNum: result.data.count });
      })
      .catch((error) => console.log("error", error));
  };

  handleBad = ({ target: { badNum } }) => {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", localStorage.getItem("authorization"));

    var raw = "";

    var requestOptions = {
      method: "POST",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    fetch("/api/comments/" + this.props.cmtIdx + "/dislike", requestOptions)
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        if (result.error_code !== null) {
          if (result.error_code === 44) {
            ReToken.ReToken();
            return;
          }

          if (result.error_code === 42) {
            // 로그인 필요한 경우
            if (!localStorage.getItem("authorization")) {
              alert("로그인이 필요한 기능입니다, 로그인 페이지로 이동합니다.");
              window.location.href = "/login";
            } else {
              alert("잘못된 접근입니다.");
            }
            return;
          }

          alert(result.message);
          return;
        }

        this.setState({ badNum: result.data.count });
      })
      .catch((error) => console.log("error", error));
  };

  render() {
    const { goodNum, badNum } = this.state;

    return (
      <div>
        <h5>{this.props.nickname}</h5>
        <span style={{ fontSize: 15 }}>
          {this.props.comment}
          <br />
        </span>
        <div style={{ display: "flex" }}>
          <span style={{ fontSize: 10, paddingTop: 20 }}>
            {this.props.date}
          </span>
          <div style={{ marginLeft: 580, display: "flex" }}>
            <Button
              variant="contained"
              onClick={this.handleGood}
              style={{ marginRight: 10, width: 50 }}
            >
              <img src="/Icon/commentGood.png" alt="icon" />
              &ensp;{goodNum}
            </Button>
            <Button
              variant="contained"
              onClick={this.handleBad}
              style={{ marginRight: 5, width: 50 }}
            >
              <img src="/Icon/commentBad.png" alt="icon" />
              &ensp;{badNum}
            </Button>
          </div>
          <div style={{ display: "none" }}>{this.props.cmtIdx}</div>
          <div style={{ display: "none" }}>{this.props.goodNum}</div>
          <div style={{ display: "none" }}>{this.props.badNum}</div>
        </div>
      </div>
    );
  }
}

export default Comment;
