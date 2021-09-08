import React, { useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
//아이디,비번 입력창
import TextField from "@material-ui/core/TextField";
//로그인 버튼
import Button from "@material-ui/core/Button";
// 성별 선택
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
// 토큰 재발급
var ReToken = require("../AuthRoute");

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(8, 75),
  },
  textField: {
    "& > *": {
      margin: theme.spacing(1),
      width: 300,
    },
  },
  smallTextField: {
    margin: theme.spacing(1),
    width: 142,
  },
  display: {
    display: "flex",
  },
  loginButton: {
    "& > *": {
      margin: theme.spacing(1, 1, 1, 1),
      width: 300,
      height: 40,
    },
  },
}));

export default function EditInfo() {
  const classes = useStyles();

  const [password, setPassword] = useState("");
  const [passwordCheck, setPasswordCheck] = useState("");
  const [name, setName] = useState(localStorage.getItem("name"));
  const [gender, setGender] = React.useState(localStorage.getItem("gender"));
  const [birth, setBirth] = useState(localStorage.getItem("birth"));

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };
  const handlePasswordCheckChange = (e) => {
    setPasswordCheck(e.target.value);
  };
  const handleNameChange = (e) => {
    setName(e.target.value);
  };
  const handleGenderChange = (e) => {
    setGender(e.target.value);
  };
  const handleBirthChange = (e) => {
    setBirth(e.target.value);
  };

  const handleSubmit = () => {
    console.log(password, passwordCheck, name, gender, birth);
    if (password === "" || passwordCheck === "" || name === "" ||
    gender === "" || birth === "") {
      alert("정보를 모두 입력해주세요!!");
      console.log(password);
      console.log(passwordCheck);
      console.log(name);
      console.log(gender);
    } else if (password !== passwordCheck) {
      alert("비밀번호가 일치하지 않습니다!!");
    } else if (password.length < 8 || password.length > 20) {
      alert("비밀번호는 8자리 이상 20자 이하로 설정해주세요!");
    } else {
      var myHeaders = new Headers();
      myHeaders.append("Content-Type", "application/json");
      myHeaders.append("Authorization", `Bearer ${localStorage.getItem("authorization")}`);
      

      var raw = JSON.stringify({
        password: password,
        name: name,
        birth: birth,
        gender: gender,
      });

      var requestOptions = {
        method: "PUT",
        headers: myHeaders,
        body: raw,
        redirect: "follow",
      };

      var userIdx = localStorage.getItem("user_idx");

      fetch("/api/users/" + userIdx, requestOptions)
        .then((response) => response.json())
        .then((result) => {
          console.log(result);

          if (result.error_code !== null) {
            if (result.error_code === "A005") {
              ReToken.ReToken();
              return;
            }
  
            if (!(localStorage.getItem("AUTHORIZATION"))) {
              alert("로그인이 필요한 기능입니다, 로그인 페이지로 이동합니다.")
              window.location.href = "/login";
              return;
            }
  
            alert("잘못된 접근입니다");
            return;
          }
        })
        .catch((error) => console.log("error", error));
    }
  };

  const handleOut = () => {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", `Bearer ${localStorage.getItem("authorization")}`);

    var raw = "";

    var requestOptions = {
      method: "DELETE",
      headers: myHeaders,
      body: raw,
      redirect: "follow",
    };

    var userIdx = localStorage.getItem("user_idx");

    fetch("/api/users/" + userIdx, requestOptions)
      .then((response) => response.json())
      .then((result) => {
        console.log(result);

        if (result.error_code !== null) {
          if (result.error_code === "A005") {
            ReToken.ReToken();
            return;
          }

          if (!(localStorage.getItem("AUTHORIZATION"))) {
            alert("로그인이 필요한 기능입니다, 로그인 페이지로 이동합니다.")
            window.location.href = "/login";
            return;
          }

          alert("잘못된 접근입니다");
          return;
        }

        alert("회원 탈퇴가 성공적으로 마무리되었습니다.");
        localStorage.clear();
        window.location.href = "/";
      })
      .catch((error) => console.log("error", error));
  };

  const inputLabel = React.useRef(null);
  const [labelWidth, setLabelWidth] = React.useState(0);
  React.useEffect(() => {
    setLabelWidth(inputLabel.current.offsetWidth);
  }, []);

  return (
    <div className={classes.root}>
      <div
        style={{
          color: "#ff7043",
          fontSize: "64px",
          fontWeight: 700,
          cursor: "hand",
        }}
      >
        &emsp;WWW
      </div>

      <form className={classes.textField} noValidate autoComplete="off">
        <TextField
          disabled
          id="account"
          label="아이디"
          defaultValue={localStorage.getItem("account")}
          variant="outlined"
        />
        <TextField
          id="password"
          label="비밀번호"
          value={password}
          onChange={handlePasswordChange}
          type="password"
          variant="outlined"
        />
        <TextField
          id="passwordCheck"
          label="비밀번호 재입력"
          value={passwordCheck}
          onChange={handlePasswordCheckChange}
          type="password"
          variant="outlined"
        />
      </form>

      <div className={classes.display}>
        <form className={classes.smallTextField} noValidate autoComplete="off">
          <TextField
            id="name"
            label="이름"
            value={name}
            onChange={handleNameChange}
            variant="outlined"
            placeholder={localStorage.getItem("name")}
          />
        </form>

        <FormControl variant="outlined" className={classes.smallTextField}>
          <InputLabel ref={inputLabel} id="gender_label">
            성별
          </InputLabel>
          <Select
            labelId="gender_label"
            id="gender"
            value={gender}
            onChange={handleGenderChange}
            labelWidth={labelWidth}
            placeholder={localStorage.getItem("gender")}
          >
            <MenuItem value={"Male"}>남</MenuItem>
            <MenuItem value={"Female"}>여</MenuItem>
          </Select>
        </FormControl>
      </div>

      <form className={classes.textField} noValidate autoComplete="off">
        <TextField
          id="birth"
          label="생년월일"
          type="date"
          value={birth}
          onChange={handleBirthChange}
          variant="outlined"
          InputLabelProps={{
            shrink: true,
          }}
        />
        <TextField
          disabled
          id="email"
          label="이메일"
          defaultValue={localStorage.getItem("email")}
          variant="outlined"
        />
      </form>

      <div className={classes.loginButton}>
        <Button variant="contained" color="primary" onClick={handleSubmit}>
          <span style={{ color: "#fafafa", fontWeight: "bold" }}>
            회원정보수정
          </span>
        </Button>
        <Button variant="contained" onClick={handleOut}>
          <span style={{ fontWeight: "bold" }}>회원 탈퇴</span>
        </Button>
      </div>
    </div>
  );
}